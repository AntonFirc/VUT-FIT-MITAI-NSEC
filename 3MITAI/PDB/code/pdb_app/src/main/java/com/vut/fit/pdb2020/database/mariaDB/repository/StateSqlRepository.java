package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.StateSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateSqlRepository extends JpaRepository<StateSql, String> {

    StateSql findById(Long id);

    StateSql findByName(String name);

    List<StateSql> findAll();

    void deleteById(Long id);

}
