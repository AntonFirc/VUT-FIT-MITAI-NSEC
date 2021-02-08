package com.vut.fit.pdb2020.database.dto.converter;

import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserPostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.UserPostRepository;
import com.vut.fit.pdb2020.database.dto.PostDetailDto;
import com.vut.fit.pdb2020.database.dto.UserCreateDto;
import com.vut.fit.pdb2020.database.dto.UserDetailDto;
import com.vut.fit.pdb2020.database.dto.UserDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.StateSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserPageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PostSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.StateSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserPageSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoConverter {

    @Autowired
    private StateSqlRepository stateSqlRepository;

    @Autowired
    UserPageSqlRepository userPageSqlRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PostDetialDtoConverter postDetialDtoConverter;

    @Autowired
    PostSqlRepository postSqlRepository;

    @Autowired
    UserPostRepository userPostRepository;

    public UserSql userDtoToSql(UserCreateDto userCreateDto) {

        UserSql userSql = null;

        if (userCreateDto != null) {
            StateSql stateSql = stateSqlRepository.findById(userCreateDto.getStateId());

            userSql = new UserSql(
                    userCreateDto.getName(),
                    userCreateDto.getSurname(),
                    userCreateDto.getEmail(),
                    passwordEncoder.encode(userCreateDto.getPassword()),
                    userCreateDto.getGender(),
                    userCreateDto.getAddress(),
                    userCreateDto.getCity(),
                    stateSql
            );
        }

        return userSql;

    }

    public UserCql userDtoToCql(UserCreateDto userCreateDto) {

        UserCql userCql = null;

        if (userCreateDto != null) {
            userCql = new UserCql();
            userCql.setEmail(userCreateDto.getEmail());
            userCql.setName(userCreateDto.getName());
            userCql.setSurname(userCreateDto.getSurname());
            userCql.setPassword_hash(passwordEncoder.encode(userCreateDto.getPassword()));
        }

        return userCql;

    }

    public UserDetailDto userSqlToDetail(UserSql userSql) {

        UserDetailDto userDetailDto = null;

        if (userSql != null) {
            List<UserPageSql> ownedPages = userPageSqlRepository.findUserPageSqlByUser(userSql);
            List<Long> ownedPagesIds = ownedPages.stream().map((item) -> item.getPage().getId()).collect(Collectors.toList());

            List<PostSql> pagePosts = postSqlRepository.findAllByUser(userSql);

            pagePosts.sort(Comparator.comparing(PostSql::getCreated_at).reversed());

            List<PostDetailDto> postDtos = pagePosts.stream().map(post -> postDetialDtoConverter.postSqlToDto(post)).collect(Collectors.toList());

            userDetailDto = new UserDetailDto(
                userSql.getName(),
                userSql.getSurname(),
                userSql.getProfilePath(),
                userSql.getProfilePhotoPath(),
                false, //set to false because user withdrawn from source of truth, thus inactive for more than 2w
                    ownedPagesIds,
                    postDtos
            );
        }

        return userDetailDto;
    }

    public UserDetailDto userCqlToDetail(UserCql userCql) {

        UserDetailDto userDetailDto = null;

        if (userCql != null) {
            List<UserPostCql> pagePosts = userPostRepository.findByUserEmailAndContentTypeOrderByCreatedAt(userCql.getEmail(), "text");
            List<UserPostCql> pageImgPosts = userPostRepository.findByUserEmailAndContentTypeOrderByCreatedAt(userCql.getEmail(), "image");

            pagePosts.addAll(pageImgPosts);
            pagePosts.sort(Comparator.comparing(UserPostCql::getCreated_at).reversed());

            List<PostDetailDto> postDtos = pagePosts.stream().map( post -> postDetialDtoConverter.userPostCqlToDto(post)).collect(Collectors.toList());

            userDetailDto = new UserDetailDto(
                    userCql.getName(),
                    userCql.getSurname(),
                    userCql.getProfile_path(),
                    userCql.getProfile_photo_path(),
                    userCql.getStatus(),
                    userCql.getOwned_pages(),
                    postDtos
            );
        }

        return userDetailDto;
    }

    public UserDto userCqlToBasic(UserCql userCql) {

        UserDto userDto = null;

        if (userCql != null) {

            userDto = new UserDto(
                    userCql.getName(),
                    userCql.getSurname(),
                    userCql.getProfile_path(),
                    userCql.getProfile_photo_path(),
                    userCql.getStatus()
            );
        }

        return userDto;
    }

    public UserDto userSqlToBasic(UserSql userSql) {

        UserDto userDto = null;

        if (userSql != null) {

            userDto = new UserDto(
                userSql.getName(),
                userSql.getSurname(),
                userSql.getProfilePath(),
                userSql.getProfilePhotoPath(),
                false
            );
        }

        return userDto;
    }
}
