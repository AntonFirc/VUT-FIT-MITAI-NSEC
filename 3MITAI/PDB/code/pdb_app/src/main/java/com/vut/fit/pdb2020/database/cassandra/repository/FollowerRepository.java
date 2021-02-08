package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.FollowerCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.Instant;
import java.util.List;

public interface FollowerRepository extends CassandraRepository<FollowerCql, String> {

    List<FollowerCql> findAllByUserEmail(String email);

    void deleteByUserEmailAndCreatedAt(String userEmail, Instant createdAt);

}
