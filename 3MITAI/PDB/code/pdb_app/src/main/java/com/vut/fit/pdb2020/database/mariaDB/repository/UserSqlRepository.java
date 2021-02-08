package com.vut.fit.pdb2020.database.mariaDB.repository;


import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSqlRepository extends JpaRepository<UserSql, Long> {

    UserSql findByEmail(String email);

}
