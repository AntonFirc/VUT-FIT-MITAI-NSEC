package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.dto.PostLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface PostLikeServiceEventHandler {

    void createLike(PostLikeDto postLikeDto);

    void removeLike(PostLikeDto postLikeDto);

}

@Service
class PostLikeServiceEventHanderImpl implements PostLikeServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private PagePostRepository pagePostRepository;

    @KafkaListener(topics = "like-service-event")
    public void consume(String likeStr) {
        try{
            PostLikeDto postLikeDto = OBJECT_MAPPER.readValue(likeStr, PostLikeDto.class);
            if (postLikeDto.isCreate()) {
                this.createLike(postLikeDto);
            } else {
                this.removeLike(postLikeDto);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createLike(PostLikeDto postLikeDto) {

        if (postLikeDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    postLikeDto.getPostOwnerEmail(),
                    postLikeDto.getPostContentType(),
                    Instant.parse(postLikeDto.getPostCreatedAt()));

            if (userPost != null) {
                List<Like> likes = userPost.getLikes();
                if (likes == null) {
                    likes = new ArrayList<>(); }
                likes.add(postLikeDto.getLike().toLike());
                userPost.setLikes(likes);
                userPostRepository.save(userPost);
            }
        }
        else if (postLikeDto.getPostOwnerId() != null) {
            PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    postLikeDto.getPostOwnerId(),
                    postLikeDto.getPostContentType(),
                    Instant.parse(postLikeDto.getPostCreatedAt()));

            if (pagePost != null) {
                List<Like> likes = pagePost.getLikes();
                if (likes == null) {
                    likes = new ArrayList<>(); }
                likes.add(postLikeDto.getLike().toLike());
                pagePost.setLikes(likes);
                pagePostRepository.save(pagePost);
            }
        }

    }

    public void removeLike(PostLikeDto postLikeDto) {

        if (postLikeDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    postLikeDto.getPostOwnerEmail(),
                    postLikeDto.getPostContentType(),
                    Instant.parse(postLikeDto.getPostCreatedAt()));

            if (userPost != null) {
                TreeSet<Like> likes = userPost.getTreeLikes();
                assert likes != null;
                likes.removeIf(like -> like.getId().equals(postLikeDto.getLike().getId()));
                userPost.setTreeLikes(likes);
                userPostRepository.save(userPost);
            }
        }
        else if (postLikeDto.getPostOwnerId() != null) {
            PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    postLikeDto.getPostOwnerId(),
                    postLikeDto.getPostContentType(),
                    Instant.parse(postLikeDto.getPostCreatedAt()));

            if (pagePost != null) {
                TreeSet<Like> likes = pagePost.getTreeLikes();
                assert likes != null;
                likes.removeIf(like -> like.getId().equals(postLikeDto.getLike().getId()));
                pagePost.setTreeLikes(likes);
                pagePostRepository.save(pagePost);
            }
        }

    }

}
