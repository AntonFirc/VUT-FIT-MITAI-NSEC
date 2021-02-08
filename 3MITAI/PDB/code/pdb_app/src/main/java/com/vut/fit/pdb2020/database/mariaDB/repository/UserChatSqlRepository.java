package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.ChatSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserChatSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatSqlRepository extends JpaRepository<UserChatSql, Long> {

    List<UserChatSql> findAllByUser(UserSql userSql);

    UserChatSql findByUserAndChat(UserSql userSql, ChatSql chatSql);
}
