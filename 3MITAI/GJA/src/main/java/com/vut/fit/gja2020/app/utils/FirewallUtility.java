package com.vut.fit.gja2020.app.utils;

import com.vut.fit.gja2020.app.models.IpAddress;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.repository.IpAddressRepository;
import com.vut.fit.gja2020.app.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.net.util.IPAddressUtil;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirewallUtility {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    IpAddressRepository ipRepository;

    /**
     * Add IP address provided by student from within student section as allowed to connect. Changes are made to iptables
     * and /etc/hosts.allow
     *
     * @param login
     * @param ipAddress
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean addStudentIp(String login, String ipAddress) throws IOException, InterruptedException {

        Student student = studentRepository.findByLogin(login);
        assert student != null;

        IpAddress ipAddressDB = new IpAddress();
        ipAddressDB.setStudent(student);
        ipAddressDB.setIpAddress(ipAddress);
        ipRepository.save(ipAddressDB);

        this.addHostsEntry(login, ipAddress);
        this.addIpTablesEntry(ipAddress);

        return true;
    }

    /**
     * Adds entry to iptables to allow students IP
     * @param ipAddress
     * @throws IOException
     * @throws InterruptedException
     */
    protected void addIpTablesEntry(String ipAddress) throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();

        Process proc = runtime.exec(String.format("sudo iptables -A INPUT -s %s -j ACCEPT", ipAddress));
        proc.waitFor();

    }

    /**
     * Add entry to /etc/host.allow to allow students IP
     *
     * @param login
     * @param ipAddress
     * @throws IOException
     * @throws InterruptedException
     */
    protected void addHostsEntry(String login,String ipAddress) throws IOException, InterruptedException {

        if(!IPAddressUtil.isIPv4LiteralAddress(ipAddress)) {
            throw new UnknownHostException();
        }

        List<String> fileLines = new ArrayList<>();

        Runtime runtime = Runtime.getRuntime();

        Process proc = runtime.exec("sudo cat /etc/hosts.allow");
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            fileLines.add(line);

            if (line.equals("#studentsStart")) {
                fileLines.add(String.format("sshd : %s : allow  #%s", ipAddress, login));
            }
        }
        proc.waitFor();

        StringBuffer sb = new StringBuffer();
        for(String hostsLine : fileLines) {
            sb.append(hostsLine);
            sb.append("\n");
        }
        String fileContent = sb.toString();

        String[] cmd = {
                "/bin/sh",
                "-c",
                String.format("echo \"%s\" | sudo tee /etc/hosts.allow > /dev/null", fileContent)
        };
        proc = runtime.exec(cmd);
        proc.waitFor();

    }

    /**
     * Removes access for all students from both /etc/hosts.allow and IP tables.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean removeStudentAccess() throws IOException, InterruptedException {

        List<String> fileLines = new ArrayList<>();

        Runtime runtime = Runtime.getRuntime();

        Process proc = runtime.exec("sudo cat /etc/hosts.allow");
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        boolean ignore = false;
        String line;
        while ((line = in.readLine()) != null) {

            if (!ignore) {
                fileLines.add(line);
            }

            if (line.equals("#studentsStart")) {
                ignore = true;
                continue;
            }

            if (line.equals("#studentsEnd")) {
                ignore = false;
                fileLines.add(line);
            }

        }
        proc.waitFor();

        StringBuffer sb = new StringBuffer();
        for(String hostsLine : fileLines) {
            sb.append("\n");
            sb.append(hostsLine);
        }
        String fileContent = sb.toString();

        String[] cmd = {
                "/bin/sh",
                "-c",
                String.format("echo \"%s\" | sudo tee /etc/hosts.allow > /dev/null", fileContent)
        };
        proc = runtime.exec(cmd);
        proc.waitFor();

        List<IpAddress> ipAddresses = ipRepository.findAll();
        for (IpAddress ipAddress : ipAddresses) {
            proc = runtime.exec(String.format("sudo iptables -D INPUT -s %s -j ACCEPT", ipAddress.getIpAddress()));
            proc.waitFor();
            ipRepository.delete(ipAddress);
        }

        return true;
    }

}
