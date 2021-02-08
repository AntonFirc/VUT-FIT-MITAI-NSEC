package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowerCql;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowsCql;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowsPageCql;
import com.vut.fit.pdb2020.database.cassandra.repository.FollowerRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.FollowsPageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.FollowsRepository;
import com.vut.fit.pdb2020.database.dto.SubscriptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


public interface SubscriptionServiceEventHandler {

    void subscribeTo(SubscriptionDto subscriptionDto) throws Exception;

    void unsubscribe(SubscriptionDto subscriptionDto) throws Exception;

}

@Service
class SubscriptionServiceEventHandlerImpl implements SubscriptionServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    FollowerRepository followerRepository;

    @Autowired
    FollowsRepository followsRepository;

    @Autowired
    FollowsPageRepository followsPageRepository;

    @KafkaListener(topics = "subscription-service-event")
    public void consume(String userStr) {
        try{
            SubscriptionDto subscriptionDto = OBJECT_MAPPER.readValue(userStr, SubscriptionDto.class);
            if (subscriptionDto.isDelete()) {
                this.unsubscribe(subscriptionDto);
            }
            else {
                this.subscribeTo(subscriptionDto);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void subscribeTo(SubscriptionDto subscriptionDto) throws Exception {

        if (subscriptionDto.getIsFollowedEmail() != null) {
            this.subscribeToUser(subscriptionDto);
        }
        else if (subscriptionDto.getIsFollowedId() != null) {
            this.subscribeToPage(subscriptionDto);
        }
        else {
            throw new Exception();
        }
    }

    @Override
    public void unsubscribe(SubscriptionDto subscriptionDto) throws Exception {

        if (subscriptionDto.getIsFollowedEmail() != null) {
            this.unsubscribeUser(subscriptionDto);
        }
        else if (subscriptionDto.getIsFollowedId() != null) {
            this.unsubscribePage(subscriptionDto);
        }
        else {
            throw new Exception();
        }

    }

    private void subscribeToUser(SubscriptionDto subscriptionDto) {

        FollowsCql follows = subscriptionDto.toFollowsCql();
        followsRepository.save(follows);

        FollowerCql follower = subscriptionDto.toFollowerCql();
        followerRepository.save(follower);

    }

    private void subscribeToPage(SubscriptionDto subscriptionDto) {

        FollowsPageCql follows = subscriptionDto.toFollowsPageCql();
        followsPageRepository.save(follows);

        FollowerCql follower = subscriptionDto.toFollowerCql();
        followerRepository.save(follower);

    }

    private void unsubscribeUser(SubscriptionDto subscriptionDto) {

        followsRepository.deleteByFollowsEmailAndUserEmail(subscriptionDto.getIsFollowedEmail(), subscriptionDto.getUserEmail());

        List<FollowerCql> followersCql = followerRepository.findAllByUserEmail(subscriptionDto.getUserEmail());

        for (FollowerCql followerCql : followersCql) {
            if (followerCql.getFollower_email() != null && followerCql.getFollower_email().equals(subscriptionDto.getIsFollowedEmail()))
                followerRepository.deleteByUserEmailAndCreatedAt(followerCql.getUser_email(), followerCql.getCreated_at());
        }

    }

    private void unsubscribePage(SubscriptionDto subscriptionDto) {

        followsPageRepository.deleteByFollowsIdAndUserEmail(subscriptionDto.getIsFollowedId(), subscriptionDto.getUserEmail());

        List<FollowerCql> followersCql = followerRepository.findAllByUserEmail(subscriptionDto.getUserEmail());

        for (FollowerCql followerCql : followersCql) {
            if (followerCql.getFollower_id() != null && followerCql.getFollower_id().equals(subscriptionDto.getIsFollowedId()))
                followerRepository.deleteByUserEmailAndCreatedAt(followerCql.getUser_email(), followerCql.getCreated_at());
        }

    }

}
