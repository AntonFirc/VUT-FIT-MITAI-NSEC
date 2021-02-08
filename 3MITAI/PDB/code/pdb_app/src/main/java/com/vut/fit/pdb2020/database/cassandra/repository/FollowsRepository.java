package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.FollowsCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface FollowsRepository extends CassandraRepository<FollowsCql, String> {

    List<FollowsCql> findAllByFollowsEmail(String follows_email);

    void deleteByFollowsEmailAndUserEmail(String followsEmail, String userEmail);

}
