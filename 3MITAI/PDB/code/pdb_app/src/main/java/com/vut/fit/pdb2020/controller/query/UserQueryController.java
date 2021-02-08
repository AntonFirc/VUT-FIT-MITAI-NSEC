package com.vut.fit.pdb2020.controller.query;

import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.dto.UserDetailDto;
import com.vut.fit.pdb2020.database.dto.converter.UserDtoConverter;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;

import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserQueryController {
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @PostMapping("/user/login")
    public String userLogin(@RequestParam String email, @RequestParam String password) throws Exception {

        assert email != null && password != null;

        UserCql userCql = userRepository.findByEmail(email);

        if (userCql == null) {
            UserSql userSql = userSqlRepository.findByEmail(email);
            if (userSql == null)
                throw new BadCredentialsException("Bad credentials");

            userCql = new UserCql(userSql);

            userCql.setOwned_pages(pageSqlRepository.findAllByAdminId(userSql.getId()).stream().map(PageSql::getId).collect(Collectors.toList()));
            userCql.setStatus(true);

            userRepository.save(userCql);

        }

        if (!passwordEncoder.matches(password, userCql.getPassword_hash()))
            throw new BadCredentialsException("Bad credentials");

        userCql.setLast_active(Instant.now());
        userCql.setStatus(true);
        userRepository.save(userCql);

        return "Logged in!";
    }

    @PostMapping("/user/logout")
    public String userLogout(@RequestParam String email) {

        assert email != null;

        UserCql userCql = userRepository.findByEmail(email);

        if (userCql == null)
            throw new BadCredentialsException("User does not exist!");

        if (userCql.getStatus()) {
            userCql.setStatus(false);
            userCql.setLast_active(Instant.now());
            userRepository.save(userCql);
        }

        return "Logged out";
    }

    @Transactional
    @GetMapping("/user/{profileSlug}")
    public UserDetailDto getUserProfile(@PathVariable String profileSlug) {

        assert profileSlug != null;

        String profilePath = String.format("/user/%s", profileSlug).toLowerCase();
        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);

        assert profileDictionaryCql != null;

        UserCql userCql = userRepository.findByEmail(profileDictionaryCql.getUser_email());
        if (userCql == null) {
            UserSql userSql = userSqlRepository.findByEmail(profileDictionaryCql.getUser_email());
            assert userSql != null;
            return userDtoConverter.userSqlToDetail(userSql);
        }

        return userDtoConverter.userCqlToDetail(userCql);

    }

    @GetMapping("/users/active")
    public List<UserCql> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/all")
    public List<UserSql> getActiveUsers() {
        return userSqlRepository.findAll();
    }

}
