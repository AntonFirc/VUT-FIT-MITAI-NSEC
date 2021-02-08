package com.vut.fit.pdb2020.database.dto;

import java.util.List;

public class DefaultWallDto {

    private String email;
    private List<PostDetailDto> posts;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PostDetailDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDetailDto> posts) {
        this.posts = posts;
    }
}
