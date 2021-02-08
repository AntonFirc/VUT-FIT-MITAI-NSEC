package com.vut.fit.gja2020.app.dto;

import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.StudentGroupConnector;
import com.vut.fit.gja2020.app.repository.StudentGroupConnectorRepository;
import org.eclipse.jetty.util.UrlEncoded;

import java.util.List;

public class ProjectDetailDto {

    private Long id;

    private String name;
    private String leaderLogin;
    private Boolean projectFinished;
    private String memberLogins;

    private String osGroupName;
    private String workDirectory;
    private String submitDirectory;


    public ProjectDetailDto() {}

    public ProjectDetailDto(ProjectGroup group, StudentGroupConnectorRepository sgConnectorRepository) {
        id = group.getId();
        name = UrlEncoded.decodeString(group.getName());
        leaderLogin = group.getLeaderLogin();
        projectFinished = group.getProjectFinished();

        osGroupName = group.getOsGroupName();
        submitDirectory = group.getSubmitDirectory();
        workDirectory = group.getWorkDirectory();

        List<StudentGroupConnector> connectors = sgConnectorRepository.findAllByGroup(group);

        memberLogins = "";

        for (StudentGroupConnector connector : connectors) {
            memberLogins = memberLogins.concat(String.format("%s ", connector.getStudent().getLogin()));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderLogin() {
        return leaderLogin;
    }

    public void setLeaderLogin(String leaderLogin) {
        this.leaderLogin = leaderLogin;
    }

    public Boolean getProjectFinished() {
        return projectFinished;
    }

    public void setProjectFinished(Boolean projectFinished) {
        this.projectFinished = projectFinished;
    }

    public String getOsGroupName() {
        return osGroupName;
    }

    public void setOsGroupName(String osGroupName) {
        this.osGroupName = osGroupName;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }

    public String getSubmitDirectory() {
        return submitDirectory;
    }

    public void setSubmitDirectory(String submitDirectory) {
        this.submitDirectory = submitDirectory;
    }

    public String getMemberLogins() {
        return memberLogins;
    }

    public void setMemberLogins(String memberLogins) {
        this.memberLogins = memberLogins;
    }

}
