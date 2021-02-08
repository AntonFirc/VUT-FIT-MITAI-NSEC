package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.CommentLikeSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.CommentSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeSqlRepository extends JpaRepository<CommentLikeSql, String> {

    CommentLikeSql findByUserAndComment(UserSql user, CommentSql comment);

}
