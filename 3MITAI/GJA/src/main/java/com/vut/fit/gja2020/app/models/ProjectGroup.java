package com.vut.fit.gja2020.app.models;

import org.eclipse.jetty.util.UrlEncoded;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ProjectGroup implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String leaderLogin;
    private Boolean projectFinished;

    private String osGroupName;
    private String workDirectory;
    private String submitDirectory;

    @OneToOne
    @JoinColumn(name = "courseId")
    private Course course;

    public ProjectGroup() {
        this.projectFinished = false;
    }

    public String toString() {
        return id.toString() + "/" + UrlEncoded.decodeString(name) + "/" + leaderLogin;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Boolean getProjectFinished() {
        return projectFinished;
    }

    public void setProjectFinished(Boolean projectFinished) {
        this.projectFinished = projectFinished;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public String getOsGroupName() {
        return osGroupName;
    }

    public void setOsGroupName(String osGroupName) {
        this.osGroupName = osGroupName;
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

}
