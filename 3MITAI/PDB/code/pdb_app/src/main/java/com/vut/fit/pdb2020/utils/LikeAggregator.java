package com.vut.fit.pdb2020.utils;


import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Timer;
import java.util.TreeSet;

public class LikeAggregator {

    @Autowired
    UserPostRepository userPostRepository;

    private final TreeSet<UserPostLike> userPostLikes;

    private TreeSet<PagePostLike> pagePostLikes;

    public LikeAggregator() {
        userPostLikes = new TreeSet<>();
        pagePostLikes = new TreeSet<>();
        Timer collectorTimer = new Timer();
        collectorTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                collectGarbage();
            }
        }, 120000, 120000);
    }

    public void addUserPostLike(String ownerEmail, String contentType, Instant createdAt, Like like) {

        UserPostLike userPostLike = new UserPostLike(ownerEmail, contentType, createdAt, userPostRepository);

        if (userPostLikes.contains(userPostLike)) {
            userPostLike = userPostLikes.floor(userPostLike);
            assert userPostLike != null;
            userPostLike.addLike(like);
        }
        else {
            userPostLike.addLike(like);
            userPostLikes.add(userPostLike);
        }

    }

    public void collectGarbage() {
        userPostLikes.removeIf(userPostLike -> userPostLike.getLikes().size() == 0);
    }



}
