package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageSqlRepository extends JpaRepository<PageSql, String> {

    PageSql findById(Long id);

    List<PageSql> findAllByAdminId(Long id);
}
