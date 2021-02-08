package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class PostDetailCommentDto {

    private String authorName;
    private String authorProfileUrl;
    private String authorProfilePicUrl;
    private String content;
    private List<PostDetailLikeDto> likes;
    private String createdAt;

    public PostDetailCommentDto(Comment comment) {
        authorName = comment.getAuthor_name();
        authorProfileUrl = comment.getAuthor_profile_url();
        authorProfilePicUrl = comment.getAuthor_picture_url();
        createdAt = comment.getCreated_at().toString();
        content = comment.getContent();
        likes = comment.getComment_likes().stream().map(PostDetailLikeDto::new).collect(Collectors.toList());
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PostDetailLikeDto> getLikes() {
        return likes;
    }

    public void setLikes(List<PostDetailLikeDto> likes) {
        this.likes = likes;
    }
}
