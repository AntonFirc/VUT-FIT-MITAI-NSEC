package com.vut.fit.pdb2020.database.dto;

import java.util.UUID;

public class CommentLikeDto {

    private Long id;

    private boolean create;

    private String postOwnerEmail;

    private Long postOwnerId;

    private String postContentType;

    private String postCreatedAt;

    private Long commentId;

    private LikeDto like;

    public CommentLikeDto() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        create = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
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

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public LikeDto getLike() {
        return like;
    }

    public void setLike(LikeDto like) {
        this.like = like;
    }
}
