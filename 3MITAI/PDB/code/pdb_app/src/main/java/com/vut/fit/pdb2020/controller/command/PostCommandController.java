package com.vut.fit.pdb2020.controller.command;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.*;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import com.vut.fit.pdb2020.database.mariaDB.service.CommentLikeService;
import com.vut.fit.pdb2020.database.mariaDB.service.PostCommentService;
import com.vut.fit.pdb2020.database.mariaDB.service.PostLikeService;
import com.vut.fit.pdb2020.database.mariaDB.service.PostService;
import com.vut.fit.pdb2020.utils.LikeAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@RestController
public class PostCommandController {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    PostSqlRepository postSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    LikeSqlRepository likeSqlRepository;

    @Autowired
    CommentSqlRepository commentSqlRepository;

    @Autowired
    CommentLikeSqlRepository commentLikeSqlRepository;

    @Autowired
    PostService postService;

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    PostCommentService postCommentService;

    @Autowired
    CommentLikeService commentLikeService;


    @Transactional
    @PostMapping("/user/post/create")
    public Long createUserPost(@RequestParam String email, @RequestParam String contentType, @RequestParam String textContent) throws Exception {

        assert email != null && contentType != null;

        if (contentType.equals("text") || contentType.equals("image")) {

            assert textContent != null;

            PostDto postDto = new PostDto();
            postDto.setAuthorEmail(email);
            postDto.setContentType(contentType);
            postDto.setTextContent(textContent);

            return postService.createPost(postDto);

        }

        throw new Exception("Wrong content type");

    }

    @Transactional
    @PostMapping("/user/post/delete")
    public void deleteUserPost(@RequestParam String email, @RequestParam String contentType, @RequestParam String createdAt) {

        assert email != null && contentType != null && createdAt != null;

        PostDto postDto = new PostDto();
        postDto.setDelete(true);
        postDto.setAuthorEmail(email);
        postDto.setContentType(contentType);
        postDto.setCreatedAt(createdAt);

        postService.deletePost(postDto);

    }

    @Transactional
    @PostMapping("/page/post/create")
    public Long createPagePost(@RequestParam Long pageId, @RequestParam String contentType, @RequestParam String textContent) throws Exception {

        assert pageId != null && contentType != null;

        if (contentType.equals("text") || contentType.equals("image")) {

            assert textContent != null;

            PostDto postDto = new PostDto();
            postDto.setPageId(pageId);
            postDto.setContentType(contentType);
            postDto.setTextContent(textContent);

            return postService.createPost(postDto);

        }

        throw new Exception("Wrong content type");

    }

    @Transactional
    @PostMapping("/page/post/delete")
    public String deleteUserPost(@RequestParam Long pageId, @RequestParam String contentType, @RequestParam String createdAt) {

        assert pageId != null && contentType != null && createdAt != null;

        PostDto postDto = new PostDto();
        postDto.setDelete(true);
        postDto.setPageId(pageId);
        postDto.setContentType(contentType);
        postDto.setCreatedAt(createdAt);

        postService.deletePost(postDto);

        return "Post deleted";

    }

    @Transactional
    @PostMapping("/post/like")
    public Long likePost(@RequestParam String likeGiverMail, @RequestParam Long pageId, @RequestParam String userMail, @RequestParam String contentType, @RequestParam String createdAt) throws Exception {

        assert likeGiverMail != null && contentType != null && createdAt != null;

        UserSql likeGiver;
        PostSql postSql;
        Like like = new Like();
        PostLikeDto postLikeDto = new PostLikeDto();

        if (pageId != null) {

            PageSql pageSql = pageSqlRepository.findById(pageId);
            assert pageSql != null;

            postSql = postSqlRepository.findByPageAndCreatedAt(pageSql, Instant.parse(createdAt));
            assert postSql != null;

            postLikeDto.setPostOwnerId(postSql.getPage().getId());
        }
        else if (userMail != null) {

            UserSql userSql = userSqlRepository.findByEmail(userMail);
            assert userSql != null;

            postSql = postSqlRepository.findByUserAndCreatedAt(userSql, Instant.parse(createdAt));
            assert postSql != null;

            postLikeDto.setPostOwnerEmail(postSql.getUser().getEmail());
        }
        else {
            throw new Exception();
        }

        likeGiver = userSqlRepository.findByEmail(likeGiverMail);
        assert likeGiver != null;

        LikeSql likeSql = likeSqlRepository.findByUserAndPost(likeGiver, postSql);

        if (likeSql == null) {
            likeSql = new LikeSql();
            likeSql.setPost(postSql);
            likeSql.setUser(likeGiver);
        }
        else {
            likeSql.setDeleted(true);
            likeSql.setUpdated_at(Instant.now());
            postLikeDto.setCreate(false);
        }

        likeSqlRepository.save(likeSql);

        like.setAuthorName(likeGiver.getFullName());
        like.setAuthorPictureUrl(likeGiver.getProfilePhotoPath());
        like.setAuthorProfileUrl(likeGiver.getProfilePath());
        like.setCreatedAt(Instant.now());
        like.setId(likeSql.getId());

        postLikeDto.setLike(new LikeDto(like));
        postLikeDto.setPostContentType(postSql.getContent_type());
        postLikeDto.setPostCreatedAt(postSql.getCreated_at().toString());

        postLikeService.raiseEvent(postLikeDto);

        return likeSql.getId();
    }

    @Transactional
    @PostMapping("/post/comment")
    public Long commentPost(@RequestParam String commentGiverMail, @RequestParam String content, @RequestParam Long pageId, @RequestParam String userMail, @RequestParam String contentType, @RequestParam String createdAt) throws Exception {

        assert commentGiverMail != null && contentType != null && createdAt != null;

        UserSql likeGiver;
        PostSql postSql;
        CommentDto commentDto = new CommentDto();
        PostCommentDto postCommentDto = new PostCommentDto();

        if (pageId != null) {
            PageSql pageSql = pageSqlRepository.findById(pageId);
            assert pageSql != null;

            postSql = postSqlRepository.findByPageAndCreatedAt(pageSql, Instant.parse(createdAt));
            assert postSql != null;

            postCommentDto.setPostOwnerId(postSql.getPage().getId());
        }
        else if (userMail != null) {

            UserSql user = userSqlRepository.findByEmail(userMail);
            assert user != null;

            postSql = postSqlRepository.findByUserAndCreatedAt(user, Instant.parse(createdAt));
            assert postSql != null;

            postCommentDto.setPostOwnerEmail(postSql.getUser().getEmail());

        }
        else {
            throw new Exception();
        }

        likeGiver = userSqlRepository.findByEmail(commentGiverMail);
        assert likeGiver != null;

        CommentSql commentSql = commentSqlRepository.findByUserAndPost(likeGiver, postSql);

        if (commentSql == null) {
            commentSql = new CommentSql();
            commentSql.setPost(postSql);
            commentSql.setUser(likeGiver);
            commentSql.setContent(content);

            assert content != null;
            commentDto.setContent(content);
        }
        else {
            commentSql.setDeleted(true);
            commentSql.setUpdated_at(Instant.now());
            postCommentDto.setCreate(false);
        }

        commentSqlRepository.save(commentSql);

        commentDto.setAuthorName(likeGiver.getFullName());
        commentDto.setAuthorProfileLink(likeGiver.getProfilePath());
        commentDto.setAuthorProfilePictureLink(likeGiver.getProfilePhotoPath());
        commentDto.setCreatedAt(Instant.now().toString());
        commentDto.setId(commentSql.getId());

        postCommentDto.setComment(commentDto);
        postCommentDto.setPostContentType(postSql.getContent_type());
        postCommentDto.setPostCreatedAt(postSql.getCreated_at().toString());

        postCommentService.raiseEvent(postCommentDto);

        return commentSql.getId();

    }

    @Transactional
    @PostMapping("/comment/like")
    public Long likeComment(@RequestParam String likeGiverMail, @RequestParam Long postPageId, @RequestParam String postUserMail, @RequestParam String contentType, @RequestParam String createdAt, @RequestParam Long commentId, @RequestParam String commentUserEmail ) throws Exception {

        assert likeGiverMail != null && contentType != null && createdAt != null && commentId != null;

        CommentLikeDto commentLikeDto = new CommentLikeDto();
        LikeDto likeDto = new LikeDto();
        PostSql postSql;
        CommentSql commentSql;
        UserSql likeGiver;

        if (postPageId != null) {

            PageSql pageSql = pageSqlRepository.findById(postPageId);
            assert pageSql != null;

            postSql = postSqlRepository.findByPageAndCreatedAt(pageSql, Instant.parse(createdAt));
            assert postSql != null;

            commentLikeDto.setPostOwnerId(postSql.getPage().getId());

        }
        else if (postUserMail != null) {

            UserSql userSql = userSqlRepository.findByEmail(postUserMail);
            assert userSql != null;

            postSql = postSqlRepository.findByUserAndCreatedAt(userSql, Instant.parse(createdAt));
            assert postSql != null;

            commentLikeDto.setPostOwnerEmail(postSql.getUser().getEmail());

        }
        else {
            throw new Exception();
        }

        likeGiver = userSqlRepository.findByEmail(likeGiverMail);
        assert likeGiver != null;
        UserSql commentOwner = userSqlRepository.findByEmail(commentUserEmail);
        assert commentOwner != null;
        commentSql = commentSqlRepository.findByUserAndPost(commentOwner, postSql);
        assert commentSql != null;

        CommentLikeSql commentLikeSql = commentLikeSqlRepository.findByUserAndComment(likeGiver, commentSql);

        if (commentLikeSql == null) {
            commentLikeSql = new CommentLikeSql();
            commentLikeSql.setComment(commentSql);
            commentLikeSql.setUser(likeGiver);
        }
        else {
            commentLikeSql.setDeleted(true);
            commentLikeSql.setUpdated_at(Instant.now());
            commentLikeDto.setCreate(false);
        }

        commentLikeSqlRepository.save(commentLikeSql);

        likeDto.setAuthorName(likeGiver.getFullName());
        likeDto.setAuthorProfileLink(likeGiver.getProfilePath());
        likeDto.setAuthorProfilePictureLink(likeGiver.getProfilePhotoPath());
        likeDto.setCreatedAt(Instant.now().toString());
        likeDto.setId(commentLikeSql.getId());

        commentLikeDto.setLike(likeDto);
        commentLikeDto.setPostContentType(postSql.getContent_type());
        commentLikeDto.setPostCreatedAt(postSql.getCreated_at().toString());
        commentLikeDto.setCommentId(commentSql.getId());

        commentLikeService.raiseEvent(commentLikeDto);

        return commentLikeSql.getId();
    }

}
