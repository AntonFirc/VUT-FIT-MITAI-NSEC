package com.vut.fit.pdb2020.controller.query;

import com.vut.fit.pdb2020.database.cassandra.domain.FollowerCql;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.FollowerRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.DefaultWallDto;
import com.vut.fit.pdb2020.database.dto.PostDetailDto;
import com.vut.fit.pdb2020.database.dto.converter.PostDetialDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WallController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowerRepository followerRepository;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    PagePostRepository pagePostRepository;

    @Autowired
    PostDetialDtoConverter postDetialDtoConverter;

    @GetMapping("/wall/{email}")
    public DefaultWallDto showWall(@PathVariable String email) {

        UserCql user = userRepository.findByEmail(email);
        assert user != null;

        List<FollowerCql> followList = followerRepository.findAllByUserEmail(email);
        List<PostDetailDto> wallPosts = new ArrayList<>();

        for (FollowerCql follower : followList) {
            if (follower.getFollower_email() != null) {
                List<UserPostCql> posts = userPostRepository.findByUserEmailAndContentTypeOrderByCreatedAt(follower.getFollower_email(), "text");
                posts.addAll(userPostRepository.findByUserEmailAndContentTypeOrderByCreatedAt(follower.getFollower_email(), "image"));

                List<PostDetailDto> postDtos = posts.stream().map( post -> postDetialDtoConverter.userPostCqlToDto(post)).collect(Collectors.toList());
                wallPosts.addAll(postDtos);

            }
            else if (follower.getFollower_id() != null) {
                List<PagePostCql> posts = pagePostRepository.findByPageIdAndContentTypeOrderByCreatedAt(follower.getFollower_id(), "text");
                posts.addAll(pagePostRepository.findByPageIdAndContentTypeOrderByCreatedAt(follower.getFollower_id(), "image"));

                List<PostDetailDto> postDtos = posts.stream().map( post -> postDetialDtoConverter.pagePostCqlToDto(post)).collect(Collectors.toList());
                wallPosts.addAll(postDtos);

            }
        }

        DefaultWallDto wallDto = new DefaultWallDto();
        wallDto.setEmail(email);
        wallDto.setPosts(wallPosts);

        return wallDto;

    }

}