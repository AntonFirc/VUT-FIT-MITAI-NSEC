package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.CommentSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentSqlRepository extends JpaRepository<CommentSql, String> {

    CommentSql findByUserAndPost(UserSql user, PostSql post);

}
