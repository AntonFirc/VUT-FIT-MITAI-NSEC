package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

public interface PostServiceEventHandler {

    void createPost(PostDto post);

    void deletePost(PostDto post);

}

@Service
class PostServiceEventHandlerImpl implements PostServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private PagePostRepository pagePostRepository;

    @Autowired
    private PageRepository pageRepository;

    @KafkaListener(topics = "post-service-event")
    public void consume(String userStr) {
        try{
            PostDto post = OBJECT_MAPPER.readValue(userStr, PostDto.class);
            if (post.isDelete()) {
                this.deletePost(post);
            }
            else {
                this.createPost(post);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createPost(PostDto postDto) {

        if (postDto.getAuthorEmail() != null) {
            UserPostCql post = new UserPostCql();
            post.setUser_email(postDto.getAuthorEmail());
            post.setContent_type(postDto.getContentType());
            post.setContent(postDto.getTextContent());
            post.setCreated_at(Instant.parse(postDto.getCreatedAt()));
            post.setUser_profile_path(postDto.getUserProfilePath());

            userPostRepository.save(post);
        }
        else if (postDto.getPageId() != null) {
            PageCql page = pageRepository.findById(postDto.getPageId());

            assert page != null;

            page.setLast_active(Instant.now());
            pageRepository.save(page);

            PagePostCql post = new PagePostCql();
            post.setPage_id(postDto.getPageId());
            post.setPage_name(page.getName());
            post.setContent(postDto.getTextContent());
            post.setContent_type(postDto.getContentType());
            post.setCreated_at(Instant.parse(postDto.getCreatedAt()));

            pagePostRepository.save(post);
        }

    }

    public void deletePost(PostDto postDto) {

        if (postDto.getAuthorEmail() != null) {
            UserPostCql userPostCql = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    postDto.getAuthorEmail(),
                    postDto.getContentType(),
                    Instant.parse(postDto.getCreatedAt()));

            assert userPostCql != null;

            userPostRepository.deleteByUserEmailAndContentTypeAndCreatedAt(
                    userPostCql.getUser_email(),
                    userPostCql.getContent_type(),
                    userPostCql.getCreated_at());

        }
        else if (postDto.getPageId() != null) {
            PagePostCql pagePostCql = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    postDto.getPageId(),
                    postDto.getContentType(),
                    Instant.parse(postDto.getCreatedAt()));

            assert pagePostCql != null;

            pagePostRepository.deleteByPageIdAndContentTypeAndCreatedAt(
                    pagePostCql.getPage_id(),
                    pagePostCql.getContent_type(),
                    pagePostCql.getCreated_at());
        }

    }

}
