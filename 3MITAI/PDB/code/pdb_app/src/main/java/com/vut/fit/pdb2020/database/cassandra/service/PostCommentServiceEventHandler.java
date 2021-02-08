package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.dto.PostCommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface PostCommentServiceEventHandler {

    void createComment(PostCommentDto commentDto) throws Exception;

    void removeComment(PostCommentDto commentDto);

}

@Service
class PostCommentServiceEventHandlerImpl implements PostCommentServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private PagePostRepository pagePostRepository;

    @KafkaListener(topics = "comment-service-event")
    public void consume(String likeStr) {
        try{
            PostCommentDto postCommentDto = OBJECT_MAPPER.readValue(likeStr, PostCommentDto.class);
            if (postCommentDto.isCreate()) {
                this.createComment(postCommentDto);
            } else {
                this.removeComment(postCommentDto);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createComment(PostCommentDto commentDto) throws Exception {

        if (commentDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    commentDto.getPostOwnerEmail(),
                    commentDto.getPostContentType(),
                    Instant.parse(commentDto.getPostCreatedAt()));

            if (userPost != null) {
                List<Comment> comments = userPost.getComments();
                if (comments == null) {
                    comments = new ArrayList<>(); }
                comments.add(commentDto.getComment().toComment());
                userPost.setComments(comments);
                userPostRepository.save(userPost);
            }
        }
        else if (commentDto.getPostOwnerId() != null) {
            PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    commentDto.getPostOwnerId(),
                    commentDto.getPostContentType(),
                    Instant.parse(commentDto.getPostCreatedAt()));

            if (pagePost != null) {
                List<Comment> comments = pagePost.getComments();
                if (comments == null) {
                    comments = new ArrayList<>(); }
                comments.add(commentDto.getComment().toComment());
                pagePost.setComments(comments);
                pagePostRepository.save(pagePost);
            }
        }
        else {
            throw new Exception();
        }

    }

    public void removeComment(PostCommentDto commentDto) {
        if (commentDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    commentDto.getPostOwnerEmail(),
                    commentDto.getPostContentType(),
                    Instant.parse(commentDto.getPostCreatedAt()));

            if (userPost != null) {
                TreeSet<Comment> comments = userPost.getTreeComments();
                assert comments != null;
                comments.removeIf(comment -> comment.getId().equals(commentDto.getComment().getId()));
                userPost.setTreeComments(comments);
                userPostRepository.save(userPost);
            }
        }
        else if (commentDto.getPostOwnerId() != null) {
            PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    commentDto.getPostOwnerId(),
                    commentDto.getPostContentType(),
                    Instant.parse(commentDto.getPostCreatedAt()));

            if (pagePost != null) {
                TreeSet<Comment> comments = pagePost.getTreeComments();
                assert comments != null;
                comments.removeIf(comment -> comment.getId().equals(commentDto.getComment().getId()));
                pagePost.setTreeComments(comments);
                pagePostRepository.save(pagePost);
            }
        }

    }
}
