package com.vut.fit.gja2020.app.dto;

import com.vut.fit.gja2020.app.models.ProjectGroup;

public class ProjectGroupDto {

    private Long id;
    private String name;
    private Boolean isLeader;
    private Boolean isfinished;
    private String submitFolder;
    private String workFolder;

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

    public Boolean getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }

    public String getSubmitFolder() {
        return submitFolder;
    }

    public void setSubmitFolder(String submitFolder) {
        this.submitFolder = submitFolder;
    }

    public String getWorkFolder() {
        return workFolder;
    }

    public void setWorkFolder(String workFolder) {
        this.workFolder = workFolder;
    }

    public Boolean getIsfinished() {
        return isfinished;
    }

    public void setIsfinished(Boolean isfinished) {
        this.isfinished = isfinished;
    }

}
