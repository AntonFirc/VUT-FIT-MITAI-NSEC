package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.WallSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface PostSqlRepository extends JpaRepository<PostSql, String> {

    List<PostSql> findAllByUser(UserSql user);
    List<PostSql> findAllByPage(PageSql page);
    List<PostSql> findAllByWall(WallSql wall);

    PostSql findByPageAndCreatedAt(PageSql page, Instant created_at);
    PostSql findByUserAndCreatedAt(UserSql user, Instant created_at);

}
