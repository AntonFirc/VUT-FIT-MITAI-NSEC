package com.vut.fit.pdb2020.controller.command;

import com.vut.fit.pdb2020.database.cassandra.repository.*;
import com.vut.fit.pdb2020.database.dto.SubscriptionDto;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.SubscriptionSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.Instant;

@RestController
public class SubscriptionCommandController {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionSqlRepository subscriptionSqlRepository;

    @Autowired
    FollowerRepository followerRepository;

    @Autowired
    FollowsRepository followsRepository;

    @Autowired
    FollowsPageRepository followsPageRepository;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    SubscriptionService subscriptionService;

    @Transactional
    @PostMapping("/subscribe/user")
    public Long subscribeToUser(@RequestParam String email, @RequestParam String subscribes) throws Exception {

        assert email != null && subscribes != null;

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setUserEmail(email);
        subscriptionDto.setIsFollowedEmail(subscribes);
        subscriptionDto.setCreatedAt(Instant.now().toString());

        return subscriptionService.subscribeTo(subscriptionDto);

    }

    @Transactional
    @PostMapping("/unsubscribe/user")
    public void unsubscribeUser(@RequestParam String userEmail, @RequestParam String unsubscribeFromEmail) throws Exception {

        assert userEmail != null && unsubscribeFromEmail != null;

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setUserEmail(userEmail);
        subscriptionDto.setIsFollowedEmail(unsubscribeFromEmail);
        subscriptionDto.setCreatedAt(Instant.now().toString());

        subscriptionService.unsubscribe(subscriptionDto);

    }

    @Transactional
    @PostMapping("/subscribe/page")
    public Long subscribeToPage(@RequestParam String email, @RequestParam Long subscribes) throws Exception {

        assert email != null && subscribes != null;

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setUserEmail(email);
        subscriptionDto.setIsFollowedId(subscribes);
        subscriptionDto.setCreatedAt(Instant.now().toString());

        return subscriptionService.subscribeTo(subscriptionDto);

    }

    @Transactional
    @PostMapping("/unsubscribe/page")
    public void unsubscribePage(@RequestParam Long pageId, @RequestParam String email) throws Exception {

        assert pageId != null && email != null;

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setUserEmail(email);
        subscriptionDto.setIsFollowedId(pageId);
        subscriptionDto.setCreatedAt(Instant.now().toString());

        subscriptionService.unsubscribe(subscriptionDto);

    }

}
