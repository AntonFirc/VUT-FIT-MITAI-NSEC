package com.vut.fit.pdb2020.database.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.UUID;

public class PostLikeDto {

    private Long id;

    private boolean create;

    private String postOwnerEmail;

    private Long postOwnerId;

    private String postContentType;

    private String postCreatedAt;

    private LikeDto like;

    public PostLikeDto() {

        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        create = true;
    }

    public PostLikeDto(@JsonProperty("id") Long id,
                       @JsonProperty("postOwnerEmail") String postOwnerEmail,
                       @JsonProperty("postOwnerId") Long postOwnerId,
                       @JsonProperty("postContentType") String postContentType,
                       @JsonProperty("postCreatedAt") String postCreatedAt,
                       @JsonProperty("like") LikeDto like) {
        this.id = id;
        this.postOwnerEmail = postOwnerEmail;
        this.postOwnerId = postOwnerId;
        this.postContentType = postContentType;
        this.postCreatedAt = postCreatedAt;
        this.like = like;
        create = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostOwnerEmail() {
        return postOwnerEmail;
    }

    public void setPostOwnerEmail(String postOwnerEmail) {
        this.postOwnerEmail = postOwnerEmail;
    }

    public Long getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(Long postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public String getPostContentType() {
        return postContentType;
    }

    public void setPostContentType(String postContentType) {
        this.postContentType = postContentType;
    }

    public String getPostCreatedAt() {
        return postCreatedAt;
    }

    public void setPostCreatedAt(String postCreatedAt) {
        this.postCreatedAt = postCreatedAt;
    }

    public LikeDto getLike() {
        return like;
    }

    public void setLike(LikeDto like) {
        this.like = like;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }
}