package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserPageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPageSqlRepository extends JpaRepository<UserPageSql, String> {

    UserPageSql findByUser(UserSql user);

    UserPageSql findByPage(PageSql page);

    List<UserPageSql> findUserPageSqlByUser(UserSql user);

}
