package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.UserChatCql;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatRepository extends CassandraRepository<UserChatCql, String> {

    List<UserChatCql> findAllByUserEmail(String email);

    UserChatCql findByUserEmailAndChatId(String email, Long chatId);
}
