package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    private Long id;

    private String content;

    private String authorName;

    private String authorProfileLink;

    private String authorProfilePictureLink;

    private List<LikeDto> likes;

    private String createdAt;

    public CommentDto() {}

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorName = comment.getAuthor_name();
        this.authorProfileLink = comment.getAuthor_profile_url();
        this.authorProfilePictureLink = comment.getAuthor_picture_url();
        this.likes = comment.getComment_likes().stream().map(LikeDto::new).collect(Collectors.toList());
        this.createdAt = comment.getCreated_at().toString();
    }

    public Comment toComment() {
        if (likes == null) {
            return new Comment(id, authorName, authorProfileLink, authorProfilePictureLink, content, new ArrayList<Like>(), Instant.parse(createdAt));
        }
        return new Comment(id, authorName, authorProfileLink, authorProfilePictureLink, content, likes.stream().map(LikeDto::toLike).collect(Collectors.toList()), Instant.parse(createdAt));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public List<LikeDto> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeDto> likes) {
        this.likes = likes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
