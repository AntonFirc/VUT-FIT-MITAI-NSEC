package com.vut.fit.gja2020.app.utils;

import com.vut.fit.gja2020.app.models.*;
import com.vut.fit.gja2020.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserAccountUtility {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProjectGroupRepository projectGroupRepository;

    @Autowired
    StudentGroupConnectorRepository sgConnectorRepository;

    @Autowired
    StudentCourseRepository studentCourseRepository;

    @Autowired
    CourseRepository courseRepository;

    /**
     * Creates student accounts within server OS and assigns home directories.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean createAccounts(String homeDirectoryPrefix) throws IOException, InterruptedException {

        List<Student> students = studentRepository.findAll();

        Runtime runtime = Runtime.getRuntime();

        for (Student student : students) {
            Process proc = runtime.exec(String.format("id -u %s", student.getLogin()));
            proc.waitFor();

            if (proc.exitValue() == 1) {
                student.setHomeDirectory(String.format("%s%s", homeDirectoryPrefix, student.getLogin()));
                studentRepository.save(student);
                Process userAdd = runtime.exec(String.format("sudo useradd -m -d %s -s /bin/bash %s",student.getHomeDirectory(), student.getLogin()));
                userAdd.waitFor();

                String[] cmd = {
                        "/bin/sh",
                        "-c",
                        String.format("echo \"%s:%s\" | sudo -S chpasswd",
                                student.getLogin(),
                                student.getPassword())
                };

                Process userPass = Runtime.getRuntime().exec(cmd);

                userPass.waitFor();
            }
        }

        return true;

    }

    /**
     * Removes all student accounts from system and backup their home directories.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean deleteStudentAccounts(String backupDirectoryPrefix) throws IOException, InterruptedException {

        List<Student> students = studentRepository.findAll();

        Runtime runtime = Runtime.getRuntime();

        for (Student student : students) {
            List<StudentGroupConnector> groups = sgConnectorRepository.findAllByStudent(student);
            if (groups != null && !groups.isEmpty()) {
                System.out.printf("Student %s has active groups and cannot be deleted.", student.getLogin());
                continue;
            }

            //delete student courses connection
            List<StudentCourseConnector> courses = studentCourseRepository.findAllByStudent(student);
            if (courses != null && !courses.isEmpty()) {
                courses.forEach(studentCourseRepository::delete);
            }

            Process proc = runtime.exec(String.format("sudo pkill -9 -u %s", student.getLogin()));
            proc.waitFor();
            proc = runtime.exec(String.format("sudo mkdir -p -m 770 %s", backupDirectoryPrefix));
            proc.waitFor();
            proc = runtime.exec(String.format("tar -jcvf %s%s-home-directory-backup.tar.bz2 %s",
                    backupDirectoryPrefix,
                    student.getLogin(),
                    student.getHomeDirectory()));
            proc.waitFor();
            proc = runtime.exec(String.format("sudo userdel -f -r %s", student.getLogin()));
            proc.waitFor();

            studentRepository.delete(student);
        }

        return true;

    }

    /**
     * Creates directories for each group. Two directories are created, work and submit. Assigns OS groups and privileges.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean createGroupDirectories() throws IOException, InterruptedException {

        List<ProjectGroup> groups = projectGroupRepository.findAll();
        Runtime runtime = Runtime.getRuntime();

        for (ProjectGroup group : groups) {

            Process proc = runtime.exec(String.format("sudo groupadd %s", group.getOsGroupName()));
            proc.waitFor();

            List<StudentGroupConnector> connectors = sgConnectorRepository.findAllByGroup(group);
            List<Student> students = new ArrayList<>(connectors.size());
            for (StudentGroupConnector connector : connectors) {
                students.add(connector.getStudent());
            }

            for (Student student : students) {
                Process addToGrp = runtime.exec(String.format("sudo usermod -a -G %s %s", group.getOsGroupName(), student.getLogin()));
                addToGrp.waitFor();
            }

            proc = runtime.exec(String.format("sudo mkdir -m 770 -p %s", group.getWorkDirectory()));
            proc.waitFor();
            proc = runtime.exec(String.format("sudo mkdir -m 770 -p %s", group.getSubmitDirectory()));
            proc.waitFor();

            proc = runtime.exec(String.format("sudo chown -R root:%s %s", group.getOsGroupName(), group.getWorkDirectory()));
            proc.waitFor();
            proc = runtime.exec(String.format("sudo chown -R root:%s %s", group.getOsGroupName(), group.getSubmitDirectory()));
            proc.waitFor();

        }

        return true;
    }

    /**
     * Removes groups from OS, backup and remove all group directories.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean deleteProjectGroup(String backupGroupDirectoryPrefix) throws IOException, InterruptedException {

        List<ProjectGroup> projectGroups = projectGroupRepository.findAll();

        Runtime runtime = Runtime.getRuntime();

        for (ProjectGroup group : projectGroups) {
            String year = new SimpleDateFormat("yyyy").format(new Date());
            Process proc = runtime.exec(String.format("mkdir -p -m 770 %s%s", backupGroupDirectoryPrefix, year));
            proc.waitFor();
            proc = runtime.exec(String.format("sudo tar -jcvf %s%s/%s-%s-group-directory-backup.tar.bz2 %s",
                    backupGroupDirectoryPrefix,
                    year,
                    group.getCourse().getName(),
                    group.getOsGroupName(),
                    group.getSubmitDirectory()));
            proc.waitFor();

            proc = runtime.exec(String.format("sudo rm -rf %s", group.getWorkDirectory().replace("/temp", "")));
            proc.waitFor();

            proc = runtime.exec(String.format("sudo groupdel %s", group.getOsGroupName()));
            proc.waitFor();

            List<StudentGroupConnector> connectors = sgConnectorRepository.findAllByGroup(group);
            sgConnectorRepository.deleteAll(connectors);

            projectGroupRepository.delete(group);

        }

        return true;

    }

}
