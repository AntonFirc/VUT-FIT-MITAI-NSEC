package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PostLikeDto;
import com.vut.fit.pdb2020.database.dto.SubscriptionDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.SubscribtionSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.SubscriptionSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

public interface SubscriptionService {

    public Long subscribeTo(SubscriptionDto subscriptionDto) throws Exception;

    public void unsubscribe(SubscriptionDto subscriptionDto) throws Exception;

}

@Service
class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    SubscriptionSqlRepository subscriptionSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Transactional
    public Long subscribeTo(SubscriptionDto subscriptionDto) throws Exception {

        if (subscriptionDto.getIsFollowedEmail() != null) {
            return this.subscribeToUser(subscriptionDto);
        }
        else if (subscriptionDto.getIsFollowedId() != null) {
            return this.subscribeToPage(subscriptionDto);
        }
        else {
            throw new Exception();
        }
    }

    @Transactional
    public void unsubscribe(SubscriptionDto subscriptionDto) throws Exception {

        subscriptionDto.setDelete(true);

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

    private Long subscribeToUser(SubscriptionDto subscriptionDto) {

        UserSql userSql = userSqlRepository.findByEmail(subscriptionDto.getUserEmail());
        assert userSql != null;
        UserSql subscribesTo = userSqlRepository.findByEmail(subscriptionDto.getIsFollowedEmail());
        assert subscribesTo != null;

        SubscribtionSql subscribtionSql = new SubscribtionSql();
        subscribtionSql.setSubscriber(userSql);
        subscribtionSql.setSubscribedToUser(subscribesTo);
        subscribtionSql.setCreated_at(Instant.parse(subscriptionDto.getCreatedAt()));
        subscribtionSql.setUpdated_at(Instant.parse(subscriptionDto.getCreatedAt()));
        subscriptionSqlRepository.save(subscribtionSql);

        subscriptionDto.setId(subscribtionSql.getId());

        this.raiseEvent(subscriptionDto);

        return subscribtionSql.getId();

    }

    private Long subscribeToPage(SubscriptionDto subscriptionDto) {

        UserSql userSql = userSqlRepository.findByEmail(subscriptionDto.getUserEmail());
        assert userSql != null;
        PageSql subscribesTo = pageSqlRepository.findById(subscriptionDto.getIsFollowedId());
        assert subscribesTo != null;

        SubscribtionSql subscribtionSql = new SubscribtionSql();
        subscribtionSql.setSubscriber(userSql);
        subscribtionSql.setSubscribedToPage(subscribesTo);
        subscribtionSql.setCreated_at(Instant.parse(subscriptionDto.getCreatedAt()));
        subscribtionSql.setUpdated_at(Instant.parse(subscriptionDto.getCreatedAt()));
        subscriptionSqlRepository.save(subscribtionSql);

        subscriptionDto.setId(subscribtionSql.getId());

        this.raiseEvent(subscriptionDto);

        return subscribtionSql.getId();

    }

    private void unsubscribeUser(SubscriptionDto subscriptionDto) {

        UserSql userSql = userSqlRepository.findByEmail(subscriptionDto.getUserEmail());
        assert userSql != null;
        UserSql unsubscribeFrom = userSqlRepository.findByEmail(subscriptionDto.getIsFollowedEmail());
        assert unsubscribeFrom != null;

        SubscribtionSql subscribtionSql = subscriptionSqlRepository.findBySubscriberAndSubscribedToUser(userSql, unsubscribeFrom);
        subscribtionSql.setDeleted(true);
        subscribtionSql.setUpdated_at(Instant.parse(subscriptionDto.getCreatedAt()));

        subscriptionSqlRepository.save(subscribtionSql);

        subscriptionDto.setId(subscribtionSql.getId());

        this.raiseEvent(subscriptionDto);

    }

    private void unsubscribePage(SubscriptionDto subscriptionDto) {

        PageSql pageSql = pageSqlRepository.findById(subscriptionDto.getIsFollowedId());
        assert pageSql != null;
        UserSql userSql = userSqlRepository.findByEmail(subscriptionDto.getUserEmail());
        assert userSql != null;

        SubscribtionSql subscribtionSql = subscriptionSqlRepository.findBySubscriberAndSubscribedToPage(userSql, pageSql);

        subscribtionSql.setDeleted(true);
        subscribtionSql.setUpdated_at(Instant.parse(subscriptionDto.getCreatedAt()));
        subscriptionSqlRepository.save(subscribtionSql);

        subscriptionDto.setId(subscribtionSql.getId());

        this.raiseEvent(subscriptionDto);

    }

    public void raiseEvent(SubscriptionDto dto){
        try{
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("subscription-service-event", dto.getId(), value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
