package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.SubscribtionSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionSqlRepository extends JpaRepository<SubscribtionSql, String>
{

    List<SubscribtionSql> findAllBySubscriber(UserSql subscriber);

    List<SubscribtionSql> findAllBySubscribedToUser(UserSql subscribedToUser);

    List<SubscribtionSql> findAllBySubscribedToPage(PageSql subscribedToPage);

    SubscribtionSql findBySubscriberAndSubscribedToPage(UserSql subscriber, PageSql subscribedToPage);

    SubscribtionSql findBySubscriberAndSubscribedToUser(UserSql subscriber, UserSql subscribedToUser);

}
