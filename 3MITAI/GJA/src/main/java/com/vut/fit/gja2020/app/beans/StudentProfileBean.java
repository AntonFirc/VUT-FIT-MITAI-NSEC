package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.dto.ProjectGroupDto;
import com.vut.fit.gja2020.app.models.IpAddress;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentGroupConnector;
import com.vut.fit.gja2020.app.repository.IpAddressRepository;
import com.vut.fit.gja2020.app.repository.ProjectGroupRepository;
import com.vut.fit.gja2020.app.repository.StudentGroupConnectorRepository;
import com.vut.fit.gja2020.app.repository.StudentRepository;
import com.vut.fit.gja2020.app.utils.FirewallUtility;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@ManagedBean
@ViewScoped
public class StudentProfileBean {

    @Autowired
    ProjectGroupRepository projectGroupRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    FirewallUtility firewallUtility;

    @Autowired
    IpAddressRepository ipRepository;

    @Autowired
    StudentGroupConnectorRepository sgConnectorRepository;

    @Autowired
    Dashboard dashboard;

    private String ipAddr;

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public void addIpAddress() throws IOException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String studentLogin = auth.getName();
        if (firewallUtility.addStudentIp(studentLogin, this.ipAddr)) {
            FacesMessage message = new FacesMessage("Úspěch", "IP adresa uložena");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public List<String> getAddressList() {
        List<IpAddress> ipAddresses = ipRepository.findAll();
        List<String> ipAdressesStrings = new ArrayList<String>(ipAddresses.size());
        for (IpAddress ip : ipAddresses) {
            ipAdressesStrings.add(ip.getIpAddress());
        }

        return ipAdressesStrings;
    }

    /**
     * Retreives all project groups from system, converts them to suitable format for output.
     * Intended for use within students profile, to list project groups, student is registered to.
     *
     * @return List<ProjectGroupDto>
     */
    public List<ProjectGroupDto> getProjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String studentLogin = auth.getName();
        Student student = studentRepository.findByLogin(studentLogin);

        List<StudentGroupConnector> connectors = sgConnectorRepository.findAllByStudent(student);

        List<ProjectGroupDto> groupDtos = new ArrayList<>(connectors.size());

        for (StudentGroupConnector connector : connectors) {
            ProjectGroupDto groupDto = new ProjectGroupDto();
            groupDto.setId(connector.getGroup().getId());
            groupDto.setName(UrlEncoded.decodeString(connector.getGroup().getName()));
            groupDto.setIsLeader(studentLogin.equals(connector.getGroup().getLeaderLogin()));
            groupDto.setIsfinished(connector.getGroup().getProjectFinished());
            groupDto.setSubmitFolder(connector.getGroup().getSubmitDirectory());
            groupDto.setWorkFolder(connector.getGroup().getWorkDirectory());
            groupDtos.add(groupDto);
        }
        
        return groupDtos;
    }

    public String getLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Marks group project as submitted before the deadline. Only project group leader can do this.
     * After marking project as assigned, changes to submit folder are forbidden.
     *
     * @param groupId
     * @throws IOException
     * @throws InterruptedException
     */
    public void markAsFinished(Long groupId) throws IOException, InterruptedException {

        ProjectGroup group = projectGroupRepository.findById(groupId);
        assert group != null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert group.getLeaderLogin().equals(auth.getName());

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(String.format("sudo chmod 750 %s", group.getSubmitDirectory()));
        proc.waitFor();

        group.setProjectFinished(true);
        projectGroupRepository.save(group);

        dashboard.setGroupList(null);

        FacesMessage message = new FacesMessage("Projekt odevzdán");
        FacesContext.getCurrentInstance().addMessage(null, message);

    }

    /**
     * Marks project as not submitted, and grants access to submit folder again.
     *
     *
     * @param groupId
     * @throws IOException
     * @throws InterruptedException
     */
    public void undo(Long groupId) throws IOException, InterruptedException {

        ProjectGroup group = projectGroupRepository.findById(groupId);
        assert group != null;

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(String.format("sudo chmod 770 %s", group.getSubmitDirectory()));
        proc.waitFor();

        group.setProjectFinished(false);
        projectGroupRepository.save(group);

        dashboard.setGroupList(null);

        FacesMessage message = new FacesMessage("Hotovo", "projekt označen ako neodevzdaný");
        FacesContext.getCurrentInstance().addMessage(null, message);

    }
}
