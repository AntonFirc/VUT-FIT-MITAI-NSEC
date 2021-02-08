package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.LikeSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeSqlRepository extends JpaRepository<LikeSql, String> {

    LikeSql findByUserAndPost(UserSql user, PostSql post);

}
