package com.vut.fit.pdb2020.utils;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

public class UserPostLike implements Comparable<UserPostLike> {

    private String ownerEmail;

    private String contentType;

    private Instant createdAt;

    private List<Like> likes;

    private Timer timer;

    private UserPostRepository userPostRepository;

    public UserPostLike() {
        this.likes = new ArrayList<>();
    }

    public void addLike(Like like) {
        likes.add(like);

        if (likes.size() == 20) {
            if (timer != null) {
                timer.cancel();
            }
            this.flush();
        }
        else {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    flush();
                }
            }, 30000); //30s
        }
    }

    private void flush() {
        /*UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(this.ownerEmail, this.contentType, this.createdAt);

        if (userPost != null) {
            List<Like> postLikes = userPost.getLikes();
            if (postLikes == null) {
                postLikes = new ArrayList<>();
            }
            postLikes.addAll(this.likes);
            userPost.setLikes(postLikes);
            userPostRepository.save(userPost);
        }

        System.out.println("Flushed");*/
    }

    public UserPostLike(String ownerEmail, String contentType, Instant createdAt, UserPostRepository upr) {
        this.ownerEmail = ownerEmail;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.likes = new ArrayList<>();
        userPostRepository = upr;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public int compareTo(UserPostLike o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (this.contentType.equals(o.getContentType())) {
            if (this.ownerEmail.equals(o.getOwnerEmail())) {
                if (this.createdAt == o.getCreatedAt()) {
                    return 0;
                }
            }
        }
        return this.createdAt.compareTo(o.getCreatedAt());

    }
}
