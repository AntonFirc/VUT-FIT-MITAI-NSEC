package com.vut.fit.pdb2020.database.dto;

import java.time.Instant;

public class PostDto {

    private Long id;

    private String authorEmail;

    private boolean delete;

    private Long pageId;

    private String contentType;

    private String textContent;

    private String userProfilePath;

    private String createdAt;

    public PostDto() {
        this.delete = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getUserProfilePath() {
        return userProfilePath;
    }

    public void setUserProfilePath(String userProfilePath) {
        this.userProfilePath = userProfilePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
