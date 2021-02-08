package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.cassandra.domain.FollowerCql;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowsCql;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowsPageCql;
import com.vut.fit.pdb2020.database.mariaDB.domain.SubscribtionSql;

import java.time.Instant;

public class SubscriptionDto {

    private Long id;

    private boolean delete;

    private String userEmail;

    private String isFollowedEmail;

    private Long isFollowedId;

    private String createdAt;

    public SubscriptionDto() {
        this.delete = false;
    }

    public SubscriptionDto(SubscribtionSql subscription) {
        this.userEmail = subscription.getSubscriber().getEmail();

        if (subscription.getSubscribedToUser() != null) {
            this.isFollowedEmail = subscription.getSubscribedToUser().getEmail();
        }
        else {
            this.isFollowedId = subscription.getSubscribedToPage().getId();
        }

        this.createdAt = subscription.getCreated_at().toString();
        this.delete = false;
    }

    public FollowerCql toFollowerCql() {
        FollowerCql follower = new FollowerCql();
        follower.setUser_email(userEmail);
        if (isFollowedEmail != null)
            follower.setFollower_email(isFollowedEmail);
        if (isFollowedId != null)
            follower.setFollower_id(isFollowedId);
        follower.setCreated_at(Instant.parse(createdAt));

        return follower;
    }

    public FollowsCql toFollowsCql() {
        if (isFollowedEmail == null)
            return null;

        FollowsCql follows = new FollowsCql();
        follows.setUser_email(userEmail);
        follows.setFollows_email(isFollowedEmail);
        follows.setCreated_at(Instant.parse(createdAt));

        return follows;
    }

    public FollowsPageCql toFollowsPageCql() {
        if (isFollowedId == null)
            return null;

        FollowsPageCql follows = new FollowsPageCql();
        follows.setUser_email(userEmail);
        follows.setFollowsId(isFollowedId);
        follows.setCreated_at(Instant.parse(createdAt));

        return follows;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getIsFollowedEmail() {
        return isFollowedEmail;
    }

    public void setIsFollowedEmail(String isFollowedEmail) {
        this.isFollowedEmail = isFollowedEmail;
    }

    public Long getIsFollowedId() {
        return isFollowedId;
    }

    public void setIsFollowedId(Long isFollowedId) {
        this.isFollowedId = isFollowedId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
