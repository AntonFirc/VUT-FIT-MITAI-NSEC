package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.Instant;
import java.util.List;

public interface UserPostRepository extends CassandraRepository<UserPostCql, String> {

    List<UserPostCql> findByUserEmailAndContentTypeOrderByCreatedAt(String user_email, String content_type);

    UserPostCql findByUserEmailAndContentTypeAndCreatedAt(String user_email, String content_type, Instant created_at);

    void deleteByUserEmailAndContentTypeAndCreatedAt(String userEmail, String contentType, Instant createdAt);
}
