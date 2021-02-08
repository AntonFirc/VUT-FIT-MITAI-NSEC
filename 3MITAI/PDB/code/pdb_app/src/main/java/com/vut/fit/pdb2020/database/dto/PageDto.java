package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;

public class PageDto {

    private Long id;

    private String name;

    private String adminEmail;

    private String profilePath;

    private String profilePicPath;

    private boolean delete;

    private boolean photoUpdate;

    public PageDto() {
        this.delete = false;
    }

    public PageDto(PageSql page) {
        this.id = page.getId();
        this.name = page.getName();
        this.adminEmail = page.getAdmin().getEmail();
        this.profilePath = page.getProfilePath();
        this.profilePicPath = page.getProfilePhotoPath();
        this.delete = false;
        this.photoUpdate = false;
    }

    public PageCql toPageCql() {
        PageCql page = new PageCql();
        page.setId(id);
        page.setName(name);
        page.setAdmin_email(adminEmail);
        page.setProfile_path(profilePath);
        page.setProfile_photo_path(profilePicPath);
        return page;
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

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isPhotoUpdate() {
        return photoUpdate;
    }

    public void setPhotoUpdate(boolean photoUpdate) {
        this.photoUpdate = photoUpdate;
    }
}
