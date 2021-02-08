package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface PageRepository extends CassandraRepository<PageCql, String> {

    PageCql findById(Long id);

    void deleteById(Long id);

}
