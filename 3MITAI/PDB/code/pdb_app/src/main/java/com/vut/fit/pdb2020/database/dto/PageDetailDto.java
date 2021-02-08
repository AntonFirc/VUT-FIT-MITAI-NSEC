package com.vut.fit.pdb2020.database.dto;

import org.springframework.stereotype.Component;

import java.util.List;

public class PageDetailDto {

    private String name;

    private String admin_email;

    private String profile_photo_path;

    private List<PostDetailDto> posts;

    public PageDetailDto(String name, String admin_email, String profile_photo_path, List<PostDetailDto> posts) {
        this.name = name;
        this.admin_email = admin_email;
        this.profile_photo_path = profile_photo_path;
        this.posts = posts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin_email() {
        return admin_email;
    }

    public void setAdmin_email(String admin_email) {
        this.admin_email = admin_email;
    }

    public String getProfile_photo_path() {
        return profile_photo_path;
    }

    public void setProfile_photo_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }

    public List<PostDetailDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDetailDto> posts) {
        this.posts = posts;
    }
}
