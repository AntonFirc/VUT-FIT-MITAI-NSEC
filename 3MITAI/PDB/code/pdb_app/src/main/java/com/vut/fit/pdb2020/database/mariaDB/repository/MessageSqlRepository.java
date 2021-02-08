package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.MessageSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSqlRepository extends JpaRepository<MessageSql, String> {
}
