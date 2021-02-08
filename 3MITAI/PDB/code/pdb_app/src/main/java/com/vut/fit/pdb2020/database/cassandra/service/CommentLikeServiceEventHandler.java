package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.dto.CommentLikeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface CommentLikeServiceEventHandler {

    void createCommentLike(CommentLikeDto commentLikeDto) throws Exception;

    void removeCommentLike(CommentLikeDto commentLikeDto) throws Exception;

}

@Service
class CommentLikeServiceEventHandlerImpl implements CommentLikeServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserPostRepository userPostRepository;

    @Autowired
    private PagePostRepository pagePostRepository;

    @KafkaListener(topics = "comment-like-service-event")
    public void consume(String likeStr) {
        try{
            CommentLikeDto commentLikeDto = OBJECT_MAPPER.readValue(likeStr, CommentLikeDto.class);
            if (commentLikeDto.isCreate()) {
                this.createCommentLike(commentLikeDto);
            } else {
                this.removeCommentLike(commentLikeDto);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void createCommentLike(CommentLikeDto commentLikeDto) throws Exception {

        if (commentLikeDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    commentLikeDto.getPostOwnerEmail(),
                    commentLikeDto.getPostContentType(),
                    Instant.parse(commentLikeDto.getPostCreatedAt()));

            if (userPost != null) {
                TreeSet<Comment> comments = userPost.getTreeComments();
                assert comments != null;
                Comment userComment = comments.floor(new Comment(commentLikeDto.getCommentId()));
                if (userComment != null) {
                    List<Like> likes = userComment.getComment_likes();
                    if (likes == null) {
                        likes = new ArrayList<>(); }
                    likes.add(commentLikeDto.getLike().toLike());
                    userComment.setComment_likes(likes);
                }
                userPostRepository.save(userPost);
            }
        }
        else if (commentLikeDto.getPostOwnerId() != null) {
            PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                    commentLikeDto.getPostOwnerId(),
                    commentLikeDto.getPostContentType(),
                    Instant.parse(commentLikeDto.getPostCreatedAt()));

            if (pagePost != null) {
                TreeSet<Comment> comments = pagePost.getTreeComments();
                assert comments != null;
                Comment userComment = comments.floor(new Comment(commentLikeDto.getCommentId()));
                if (userComment != null) {
                    List<Like> likes = userComment.getComment_likes();
                    if (likes == null) {
                        likes = new ArrayList<>(); }
                    likes.add(commentLikeDto.getLike().toLike());
                    userComment.setComment_likes(likes);
                }
                pagePostRepository.save(pagePost);
            }
        } else {
            throw new Exception();
        }

    }

    @Override
    public void removeCommentLike(CommentLikeDto commentLikeDto) throws Exception {

        if (commentLikeDto.getPostOwnerEmail() != null) {
            UserPostCql userPost = userPostRepository.findByUserEmailAndContentTypeAndCreatedAt(
                    commentLikeDto.getPostOwnerEmail(),
                    commentLikeDto.getPostContentType(),
                    Instant.parse(commentLikeDto.getPostCreatedAt()));

            if (userPost != null) {
                TreeSet<Comment> comments = userPost.getTreeComments();
                assert comments != null;
                Comment userComment = comments.floor(new Comment(commentLikeDto.getCommentId()));
                if (userComment != null) {
                    List<Like> likes = userComment.getComment_likes();
                    assert likes != null;
                    likes.removeIf(like -> like.getId().equals(commentLikeDto.getLike().getId()));
                    userComment.setComment_likes(likes);
                }
                userPostRepository.save(userPost);
            }
            else if (commentLikeDto.getPostOwnerId() != null) {
                PagePostCql pagePost = pagePostRepository.findByPageIdAndContentTypeAndCreatedAt(
                        commentLikeDto.getPostOwnerId(),
                        commentLikeDto.getPostContentType(),
                        Instant.parse(commentLikeDto.getPostCreatedAt()));

                if (pagePost != null) {
                    TreeSet<Comment> comments = pagePost.getTreeComments();
                    assert comments != null;
                    Comment userComment = comments.floor(new Comment(commentLikeDto.getCommentId()));
                    if (userComment != null) {
                        List<Like> likes = userComment.getComment_likes();
                        assert likes != null;
                        likes.removeIf(like -> like.getId().equals(commentLikeDto.getLike().getId()));
                        userComment.setComment_likes(likes);
                    }
                    pagePostRepository.save(pagePost);
                }
            } else {
                throw new Exception();
            }
        }

    }
}