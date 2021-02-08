package com.vut.fit.pdb2020.database.dto;

import java.time.Instant;
import java.util.List;

public class PostDetailDto {

    private String contentType;

    private String content;

    private int likeCount;

    private List<PostDetailLikeDto> likes;

    private List<PostDetailCommentDto> comments;

    private Instant createdAt;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<PostDetailLikeDto> getLikes() {
        return likes;
    }

    public void setLikes(List<PostDetailLikeDto> likes) {
        this.likes = likes;
        this.likeCount = likes.size();
    }

    public int getLikeCount() {
        return likeCount;
    }

    public List<PostDetailCommentDto> getComments() {
        return comments;
    }

    public void setComments(List<PostDetailCommentDto> comments) {
        this.comments = comments;
    }
}
