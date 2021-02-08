package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.FollowsCql;
import com.vut.fit.pdb2020.database.cassandra.domain.FollowsPageCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface FollowsPageRepository extends CassandraRepository<FollowsPageCql, String> {

    List<FollowsPageCql> findAllByFollowsId(Long followsId);

    void deleteByFollowsIdAndUserEmail(Long followsId, String userEmail);

}
