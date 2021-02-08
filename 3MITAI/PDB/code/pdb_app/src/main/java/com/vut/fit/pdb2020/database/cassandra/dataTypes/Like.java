package com.vut.fit.pdb2020.database.cassandra.dataTypes;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.Instant;
import java.util.UUID;

@UserDefinedType
public class Like implements Comparable<Like>{

    private String author_name;

    private String author_profile_url;

    private String author_picture_url;

    private Instant created_at;

    private Long ident;

    public Like() {}

    public Like(String author_name, String author_profile_url, String author_picture_url, Instant created_at, Long id) {
        this.author_name = author_name;
        this.author_profile_url = author_profile_url;
        this.author_picture_url = author_picture_url;
        this.created_at = created_at;
        this.ident = id;
    }

    public String getAuthorName() {
        return author_name;
    }

    public void setAuthorName(String authorName) {
        this.author_name = authorName;
    }

    public String getAuthorProfileUrl() {
        return author_profile_url;
    }

    public void setAuthorProfileUrl(String authorProfileUrl) {
        this.author_profile_url = authorProfileUrl;
    }

    public String getAuthorPictureUrl() {
        return author_picture_url;
    }

    public void setAuthorPictureUrl(String authorPictureUrl) {
        this.author_picture_url = authorPictureUrl;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Instant createdAt) {
        this.created_at = createdAt;
    }

    public Long getId() {
        return ident;
    }

    public void setId(Long id) {
        this.ident = id;
    }

    @Override
    public int compareTo(Like o) {
        if (o == null) {
            throw new NullPointerException();
        }

        return ident.compareTo(o.getId());

    }
}
