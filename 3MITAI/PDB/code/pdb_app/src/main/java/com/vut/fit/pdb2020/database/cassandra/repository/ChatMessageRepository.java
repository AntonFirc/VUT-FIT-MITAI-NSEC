package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.ChatMessageCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface ChatMessageRepository extends CassandraRepository<ChatMessageCql, String> {

    List<ChatMessageCql> findAllByChatId(Long chatId);

    void deleteAllByChatId(Long id);
}
