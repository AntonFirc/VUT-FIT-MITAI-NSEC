package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.ChatCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends CassandraRepository<ChatCql, String> {

    ChatCql findById(Long id);

    void deleteById(Long id);
}
