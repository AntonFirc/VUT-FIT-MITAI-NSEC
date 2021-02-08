package com.vut.fit.pdb2020.database.cassandra.dataTypes;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.Instant;
import java.util.List;

@UserDefinedType
public class Comment implements Comparable<Comment> {

    private Long ident;

    private String author_name;

    private String author_profile_url;

    private String author_picture_url;

    private String content;

    private List<Like> comment_likes;

    private Instant created_at;

    public Comment() {}

    public Comment (Long ident) {
        this.ident = ident;
    }

    public Comment(Long ident, String author_name, String author_profile_url, String author_picture_url, String content, List<Like> comment_likes, Instant created_at) {
        this.ident = ident;
        this.author_name = author_name;
        this.author_profile_url = author_profile_url;
        this.author_picture_url = author_picture_url;
        this.content = content;
        this.comment_likes = comment_likes;
        this.created_at = created_at;
    }

    public Long getId() {
        return ident;
    }

    public void setId(Long ident) {
        this.ident = ident;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_profile_url() {
        return author_profile_url;
    }

    public void setAuthor_profile_url(String author_profile_url) {
        this.author_profile_url = author_profile_url;
    }

    public String getAuthor_picture_url() {
        return author_picture_url;
    }

    public void setAuthor_picture_url(String author_picture_url) {
        this.author_picture_url = author_picture_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Like> getComment_likes() {
        return comment_likes;
    }

    public void setComment_likes(List<Like> comment_likes) {
        this.comment_likes = comment_likes;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    @Override
    public int compareTo(Comment o) {
        if (o == null) {
            throw new NullPointerException();
        }

        return ident.compareTo(o.getId());

    }



}
