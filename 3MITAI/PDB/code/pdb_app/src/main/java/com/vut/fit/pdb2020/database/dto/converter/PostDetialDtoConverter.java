package com.vut.fit.pdb2020.database.dto.converter;

import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.dto.*;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostDetialDtoConverter {

    public PostDetailDto pagePostCqlToDto(PagePostCql post) {

        PostDetailDto postDetailDto = null;

        if ( post != null ) {
            postDetailDto = new PostDetailDto();
            postDetailDto.setContent(post.getContent());
            postDetailDto.setContentType(post.getContent_type());
            postDetailDto.setCreatedAt(post.getCreated_at());
            List<PostDetailLikeDto> postLikes = new ArrayList<>();
            if (post.getLikes() != null) {
                for (Like like :  post.getLikes()) {
                    postLikes.add(new PostDetailLikeDto(like));
                }
                postDetailDto.setLikes(postLikes);
            }

            List<PostDetailCommentDto> comments = new ArrayList<>();
            if (post.getComments() != null) {
                for (Comment comment :  post.getComments()) {
                    comments.add(new PostDetailCommentDto(comment));
                }
                postDetailDto.setComments(comments);
            }
        }

        return postDetailDto;

    }

    public PostDetailDto userPostCqlToDto(UserPostCql post) {

        PostDetailDto postDetailDto = null;

        if ( post != null ) {
            postDetailDto = new PostDetailDto();
            postDetailDto.setContent(post.getContent());
            postDetailDto.setContentType(post.getContent_type());
            postDetailDto.setCreatedAt(post.getCreated_at());
            List<PostDetailLikeDto> postLikes = new ArrayList<>();
            if (post.getLikes() != null) {
                for (Like like :  post.getLikes()) {
                    postLikes.add(new PostDetailLikeDto(like));
                }
                postDetailDto.setLikes(postLikes);
            }
        }

        return postDetailDto;

    }

    public PostDetailDto postSqlToDto(PostSql post) {

        PostDetailDto postDetailDto = null;

        if ( post != null ) {
            postDetailDto = new PostDetailDto();
            postDetailDto.setContent(post.getContent());
            postDetailDto.setContentType(post.getContent_type());
            postDetailDto.setCreatedAt(post.getCreated_at());
        }

        return postDetailDto;

    }

}
