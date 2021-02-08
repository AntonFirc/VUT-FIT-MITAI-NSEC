package com.vut.fit.pdb2020.controller.query;

import com.vut.fit.pdb2020.database.cassandra.domain.*;
import com.vut.fit.pdb2020.database.cassandra.repository.*;
import com.vut.fit.pdb2020.database.dto.NameProfileTuple;
import com.vut.fit.pdb2020.database.dto.SubscribeDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.SubscribtionSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.SubscriptionSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SubscriptionQueryController {


    @Autowired
    UserRepository userRepository;

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

    @GetMapping("/subscribers/user/{slug}")
    public SubscribeDto getSubscribers(@PathVariable String slug) {

        String profilePath = String.format("/user/%s", slug);
        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);

        String email = profileDictionaryCql.getUser_email();

        List<NameProfileTuple> subscribers = new ArrayList<>();

        List<FollowsCql> follows = followsRepository.findAllByFollowsEmail(email);

        for (FollowsCql follow : follows) {
            UserCql user = userRepository.findByEmail(follow.getUser_email());

            assert user != null;
            subscribers.add(new NameProfileTuple(String.format("%s %s", user.getName(), user.getSurname()), user.getProfile_path()));
        }

        SubscribeDto subscribeDto = new SubscribeDto();
        UserCql userCql = userRepository.findByEmail(email);
        assert userCql != null;

        subscribeDto.setTarget(new NameProfileTuple(String.format("%s %s", userCql.getName(), userCql.getSurname()), userCql.getProfile_path()));

        subscribeDto.setSubscriptions(subscribers);
        return subscribeDto;
    }

    @GetMapping("/subscribers/page/{slug}")
    public SubscribeDto getPageSubscribers(@PathVariable String slug) {

        String profilePath = "/page/".concat(slug);

        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);
        Long pageId = profileDictionaryCql.getPage_id();

        List<FollowsPageCql> follows = followsPageRepository.findAllByFollowsId(pageId);
        List<NameProfileTuple> subscribers = new ArrayList<>();

        for (FollowsPageCql follow : follows) {
            UserCql user = userRepository.findByEmail(follow.getUser_email());
            assert user != null;

            subscribers.add(new NameProfileTuple(String.format("%s %s", user.getName(), user.getSurname()), user.getProfile_path()));
        }

        SubscribeDto subscribeDto = new SubscribeDto();
        PageCql pageCql = pageRepository.findById(profileDictionaryCql.getPage_id());
        assert pageCql != null;

        subscribeDto.setTarget(new NameProfileTuple(pageCql.getName(), pageCql.getProfile_path()));

        subscribeDto.setSubscriptions(subscribers);
        return subscribeDto;
    }

    @GetMapping("/subscribed-to/{slug}")
    public SubscribeDto getSubscribedTo(@PathVariable String slug) {

        String profilePath = String.format("/user/%s", slug);

        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);
        String email = profileDictionaryCql.getUser_email();

        List<FollowerCql> follows = followerRepository.findAllByUserEmail(email);

        List<NameProfileTuple> subscribers = new ArrayList<>();

        for (FollowerCql follow : follows) {
            if (follow.getFollower_email() != null) {
                UserCql follower = userRepository.findByEmail(follow.getFollower_email());
                assert follower != null;

                NameProfileTuple tuple = new NameProfileTuple(String.format("%s %s", follower.getName(), follower.getSurname()), follower.getProfile_path());
                subscribers.add(tuple);
            }
            if (follow.getFollower_id() != null) {
                PageCql page = pageRepository.findById(follow.getFollower_id());

                subscribers.add(new NameProfileTuple(page.getName(), page.getProfile_path()));
            }

        }

        SubscribeDto subscribeDto = new SubscribeDto();
        UserCql userCql = userRepository.findByEmail(email);
        assert userCql != null;

        subscribeDto.setTarget(new NameProfileTuple(String.format("%s %s", userCql.getName(), userCql.getSurname()), userCql.getProfile_path()));

        subscribeDto.setSubscriptions(subscribers);
        return subscribeDto;
    }

}
