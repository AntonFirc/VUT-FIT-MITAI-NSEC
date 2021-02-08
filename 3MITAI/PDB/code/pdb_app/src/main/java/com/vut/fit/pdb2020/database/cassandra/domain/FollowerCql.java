package com.vut.fit.pdb2020.database.cassandra.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("user_follower")
public class FollowerCql {

    @Column("user_email")
    private String userEmail;

    @Column("follower_email")
    private String followerEmail;

    @PrimaryKey
    private Long follower_id;

    @Column("created_at")
    private Instant createdAt;

    public String getUser_email() {
        return userEmail;
    }

    public void setUser_email(String user_email) {
        this.userEmail = user_email;
    }

    public String getFollower_email() {
        return followerEmail;
    }

    public void setFollower_email(String follower_email) {
        this.followerEmail = follower_email;
    }

    public Instant getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(Instant created_at) {
        this.createdAt = created_at;
    }

    public Long getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(Long follower_id) {
        this.follower_id = follower_id;
    }
}
