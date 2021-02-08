package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.Instant;
import java.util.List;

public interface PagePostRepository extends CassandraRepository<PagePostCql, String> {

    List<PagePostCql> findByPageIdAndContentTypeOrderByCreatedAt(Long page_id, String content_type);

    PagePostCql findByPageIdAndContentTypeAndCreatedAt(Long page_id, String content_type, Instant created_at);

    void deleteByPageIdAndContentTypeAndCreatedAt(Long pageId, String contentType, Instant createdAt);

}
