package com.vut.fit.pdb2020.database.cassandra.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("user_follows")
public class FollowsCql {

    @Column("follows_email")
    private String followsEmail;

    @Column("user_email")
    private String userEmail;

    @PrimaryKey
    private Instant created_at;

    public String getFollows_email() {
        return followsEmail;
    }

    public void setFollows_email(String follows_email) {
        this.followsEmail = follows_email;
    }

    public String getUser_email() {
        return userEmail;
    }

    public void setUser_email(String user_email) {
        this.userEmail = user_email;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }
}
