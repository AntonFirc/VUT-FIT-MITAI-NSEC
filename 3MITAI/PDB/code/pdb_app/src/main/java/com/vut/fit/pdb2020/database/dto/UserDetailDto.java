package com.vut.fit.pdb2020.database.dto;

import java.util.List;

public class UserDetailDto {

    private String name;

    private String surname;

    private String profilePath;

    private String profilePhotoPath;

    private Boolean status;

    private List<Long> ownedPages;

    private List<PostDetailDto> posts;

    public UserDetailDto(String name, String surname, String profilePath, String profilePhotoPath, Boolean status, List<Long> ownedPages, List<PostDetailDto> posts) {
        this.name = name;
        this.surname = surname;
        this.profilePath = profilePath;
        this.profilePhotoPath = profilePhotoPath;
        this.status = status;
        this.ownedPages = ownedPages;
        this.posts = posts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Long> getOwnedPages() { return ownedPages; }

    public void setOwnedPages(List<Long> ownedPages) { this.ownedPages = ownedPages; }

    public List<PostDetailDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDetailDto> posts) {
        this.posts = posts;
    }
}
