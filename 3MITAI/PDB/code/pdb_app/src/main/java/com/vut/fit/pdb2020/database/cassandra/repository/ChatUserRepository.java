package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.ChatUserCql;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends CassandraRepository<ChatUserCql, String> {

    List<ChatUserCql> findAllByChatId(Long id);
}
