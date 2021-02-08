package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PostDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.PostSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

public interface PostService {
    public Long createPost(PostDto postDto) throws Exception;

    public void deletePost(PostDto postDto);
}

@Service
class PostServiceImpl implements PostService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private PostSqlRepository postSqlRepository;

    @Autowired
    private UserSqlRepository userSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Override
    public Long createPost(PostDto postDto) throws Exception {

        PostSql post = new PostSql();

        if (postDto.getAuthorEmail() != null) {
            UserSql userSql = userSqlRepository.findByEmail(postDto.getAuthorEmail());
            assert userSql != null;

            post.setUser(userSql);
            post.setWall(userSql.getWall());

            postDto.setUserProfilePath(userSql.getProfilePath());

        }
        else if (postDto.getPageId() != null) {
            PageSql pageSql = pageSqlRepository.findById(postDto.getPageId());
            assert pageSql != null;

            post.setPage(pageSql);
            post.setWall(pageSql.getWall());

            postDto.setUserProfilePath(pageSql.getProfilePath());

        }
        else {
            throw new Exception();
        }

        post.setContent(postDto.getTextContent());
        post.setContent_type(postDto.getContentType());
        post.setCreated_at(Instant.now());
        post.setUpdated_at(post.getCreated_at());
        postDto.setId(this.postSqlRepository.save(post).getId());
        postDto.setCreatedAt(post.getCreated_at().toString());
        this.raiseEvent(postDto);
        return postDto.getId();
    }

    public void deletePost(PostDto postDto) {

        if (postDto.getAuthorEmail() != null) {
            UserSql userSql = userSqlRepository.findByEmail(postDto.getAuthorEmail());
            assert userSql != null;

            PostSql postSql = postSqlRepository.findByUserAndCreatedAt(userSql, Instant.parse(postDto.getCreatedAt()));
            assert postSql != null;

            postSql.setUpdated_at(Instant.now());
            postSql.setDeleted(true);
            postSqlRepository.save(postSql);

            this.raiseEvent(postDto);
        }
        else if (postDto.getPageId() != null) {
            PageSql pageSql = pageSqlRepository.findById(postDto.getPageId());
            assert pageSql != null;

            PostSql postSql = postSqlRepository.findByPageAndCreatedAt(pageSql, Instant.parse(postDto.getCreatedAt()));
            assert postSql != null;

            postSql.setUpdated_at(Instant.now());
            postSql.setDeleted(true);
            postSqlRepository.save(postSql);

            this.raiseEvent(postDto);
        }

    }

    private void raiseEvent(PostDto post){
        try{
            String value = OBJECT_MAPPER.writeValueAsString(post);
            this.kafkaTemplate.sendDefault(post.getId(), value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
