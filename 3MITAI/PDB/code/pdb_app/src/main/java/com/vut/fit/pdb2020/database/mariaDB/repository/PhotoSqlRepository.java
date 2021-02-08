package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.PhotoSql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoSqlRepository extends JpaRepository<PhotoSql, String> {

    PhotoSql findById(Integer id);

    PhotoSql findByPath(String path);

    PhotoSql deleteByPath(String path);

}
