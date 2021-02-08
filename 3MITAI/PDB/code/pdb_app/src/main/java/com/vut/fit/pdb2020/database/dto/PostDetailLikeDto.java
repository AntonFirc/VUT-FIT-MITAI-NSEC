package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;

import java.time.Instant;

public class PostDetailLikeDto {

    private String authorName;
    private String authorProfileUrl;
    private String authorProfilePicUrl;
    private String createdAt;

    public PostDetailLikeDto(Like like) {
        this.authorName = like.getAuthorName();
        this.authorProfileUrl = like.getAuthorProfileUrl();
        this.authorProfilePicUrl = like.getAuthorPictureUrl();
        this.createdAt = like.getCreatedAt().toString();
    }

    public PostDetailLikeDto(String authorName, String authorProfileUrl, String authorProfilePicUrl, String createdAt) {
        this.authorName = authorName;
        this.authorProfileUrl = authorProfileUrl;
        this.authorProfilePicUrl = authorProfilePicUrl;
        this.createdAt = createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfileUrl() {
        return authorProfileUrl;
    }

    public void setAuthorProfileUrl(String authorProfileUrl) {
        this.authorProfileUrl = authorProfileUrl;
    }

    public String getAuthorProfilePicUrl() {
        return authorProfilePicUrl;
    }

    public void setAuthorProfilePicUrl(String authorProfilePicUrl) {
        this.authorProfilePicUrl = authorProfilePicUrl;
    }

    public Instant getCreatedAt() {
        return Instant.parse(createdAt);
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt.toString();
    }
}
