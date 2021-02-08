package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;

import java.time.Instant;

public class LikeDto {

    private Long id;

    private String authorName;

    private String authorProfileLink;

    private String authorProfilePictureLink;

    private String createdAt;

    public LikeDto() {}

    public LikeDto(Like like) {
        this.id = like.getId();
        this.authorName = like.getAuthorName();
        this.authorProfileLink = like.getAuthorProfileUrl();
        this.authorProfilePictureLink = like.getAuthorPictureUrl();
        this.createdAt = like.getCreatedAt().toString();
    }

    public Like toLike() {
        return new Like(authorName, authorProfileLink, authorProfilePictureLink, Instant.parse(createdAt), id);
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfileLink() {
        return authorProfileLink;
    }

    public void setAuthorProfileLink(String authorProfileLink) {
        this.authorProfileLink = authorProfileLink;
    }

    public String getAuthorProfilePictureLink() {
        return authorProfilePictureLink;
    }

    public void setAuthorProfilePictureLink(String authorProfilePictureLink) {
        this.authorProfilePictureLink = authorProfilePictureLink;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
