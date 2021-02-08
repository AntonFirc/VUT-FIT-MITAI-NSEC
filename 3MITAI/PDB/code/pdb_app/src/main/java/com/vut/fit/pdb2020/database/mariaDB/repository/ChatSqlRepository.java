package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.ChatSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSqlRepository extends JpaRepository<ChatSql, String> {

    ChatSql findById(Long id);

    ChatSql findByName(String name);
}
