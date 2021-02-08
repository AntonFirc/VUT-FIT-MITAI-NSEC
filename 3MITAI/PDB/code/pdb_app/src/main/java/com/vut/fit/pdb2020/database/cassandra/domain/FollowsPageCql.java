package com.vut.fit.pdb2020.database.cassandra.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("page_follows")
public class FollowsPageCql {

    @Column("follows_id")
    private Long followsId;

    @Column("user_email")
    private String userEmail;

    @PrimaryKey
    private Instant created_at;

    public Long getFollowsId() {
        return followsId;
    }

    public void setFollowsId(Long followsId) {
        this.followsId = followsId;
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
