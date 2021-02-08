package com.vut.fit.pdb2020.controller.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.UserCreateDto;
import com.vut.fit.pdb2020.database.dto.UserServiceDto;
import com.vut.fit.pdb2020.database.dto.converter.UserDtoConverter;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import com.vut.fit.pdb2020.database.mariaDB.service.UserService;
import com.vut.fit.pdb2020.utils.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
public class UserCommandController {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    PhotoSqlRepository photoSqlRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Autowired
    private UserDtoConverter userDtoConverter;

    @Autowired
    UserPageSqlRepository userPageSqlRepository;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    @Autowired
    private StateSqlRepository stateSqlRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    FileUtility fileUtility;
    
    @Transactional
    @PostMapping("/user/create")
    public Long createUser(@RequestBody String userJson) throws Exception {

        UserCreateDto userCreateDto = jsonObjectMapper.readValue(userJson, UserCreateDto.class);
        
        if(userSqlRepository.findByEmail(userCreateDto.getEmail()) != null) {
            throw new Exception("User with this email already exists");
        }

        WallSql wallSql = new WallSql();
        UserSql userSql = userDtoConverter.userDtoToSql(userCreateDto);

        Instant now = Instant.now();

        wallSql = wallSqlRepository.save(wallSql);
        userSql.setWall(wallSql);
        userSql.setCreated_at(now);
        userSql.setUpdated_at(now);
        userSqlRepository.save(userSql);

        ProfileDictionarySql profileDictionarySql = new ProfileDictionarySql();
        profileDictionarySql.setUser(userSql);
        profileDictionarySql.setPath(userSql.getProfilePath());
        profileDictionarySqlRepository.save(profileDictionarySql);

        UserCql userCql = new UserCql(
                userSql.getEmail(),
                userSql.getName(),
                userSql.getSurname(),
                userSql.getPassword_hash(),
                userSql.getProfilePath(),
                userSql.getProfilePhotoPath(),
                null,
                null,
                false,
                userSql.getCreated_at()
        );

        this.createProfileDictionaryCql(userCql);

        userRepository.save(userCql);

        return userSql.getId();
    }

    @Transactional
    @PostMapping("/user/update")
    public void updateUser(@RequestBody String userJson) throws Exception {

        UserCreateDto userCreateDto = jsonObjectMapper.readValue(userJson, UserCreateDto.class);

        UserSql userSql = userSqlRepository.findByEmail(userCreateDto.getEmail());
        assert userSql != null;

        ProfileDictionarySql profileDictionarySql = profileDictionarySqlRepository.findByPath(userSql.getProfilePath());

        StateSql stateSql = stateSqlRepository.findById(userCreateDto.getStateId());

        Instant now = Instant.now();

        userSql.setName(userCreateDto.getName());
        userSql.setSurname(userCreateDto.getSurname());
        userSql.setGender(userCreateDto.getGender());
        userSql.setAddress(userCreateDto.getAddress());
        userSql.setCity(userCreateDto.getCity());
        userSql.setUpdated_at(now);
        userSql.setState(stateSql);

        profileDictionarySql.setPath(userSql.getProfilePath());
        profileDictionarySql.setUpdated_at(now);
        profileDictionarySqlRepository.save(profileDictionarySql);

        UserServiceDto userServiceDto = new UserServiceDto(userSql);

        userService.updateUser(userServiceDto);

    }

    @Transactional
    @PostMapping("/user/pass")
    public void changePassword(@RequestParam String email, @RequestParam String password) throws Exception {
        assert email != null && password != null;

        UserSql user = userSqlRepository.findByEmail(email);
        assert user != null;

        user.setPassword_hash(passwordEncoder.encode(password));
        user.setUpdated_at(Instant.now());
        userSqlRepository.save(user);

        UserServiceDto userServiceDto = new UserServiceDto();
        userServiceDto.setId(user.getId());
        userServiceDto.setEmail(email);
        userServiceDto.setPassword_hash(user.getPassword_hash());

        userService.changePassword(userServiceDto);
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String email) throws Exception {

        UserSql userSql = userSqlRepository.findByEmail(email);

        List<UserPageSql> pages = userPageSqlRepository.findUserPageSqlByUser(userSql);

        if (pages.size() != 0) {
            throw new Exception("Cannot delete user, has active pages !");
        }

        if (userSql == null)
            throw new BadCredentialsException("User does not exist!");

        userSql.setDeleted(true);
        userSql.setUpdated_at(Instant.now());
        userSqlRepository.save(userSql);

        UserServiceDto userServiceDto = new UserServiceDto(userSql);
        userService.deleteUser(userServiceDto);

        return "User deleted";
    }

    @PostMapping("/user/addProfilePic")
    public Long addProfilePic(@RequestParam String email, @RequestParam MultipartFile file ) throws IOException {

        assert email != null;

        UserSql userSql = userSqlRepository.findByEmail(email);
        assert userSql != null;

        File dest = fileUtility.saveFile(file, null, email);

        PhotoSql photoSql = new PhotoSql();

        String filePath = fileUtility.uploadsDir.concat(dest.getName());

        photoSql.setPath(filePath);
        photoSql.setUser(userSql);
        photoSql = photoSqlRepository.save(photoSql);

        userSql.setProfilePhoto(photoSql);
        userSql.setUpdated_at(Instant.now());
        userSqlRepository.save(userSql);

        UserServiceDto userServiceDto = new UserServiceDto(userSql);
        userService.addProfilePic(userServiceDto);

        return photoSql.getId();
    }

    @PostMapping("/user/removeProfilePic")
    public void removeProfilePic(@RequestParam String email) {

        assert email != null;

        UserSql userSql = userSqlRepository.findByEmail(email);
        assert userSql != null;

        PhotoSql photoSql = userSql.getProfilePhoto();
        assert photoSql != null;
        userSql.setProfilePhoto(null);
        userSqlRepository.save(userSql);

        photoSql.setDeleted(true);
        photoSql.setUpdated_at(Instant.now());
        photoSqlRepository.save(photoSql);

        UserServiceDto userServiceDto = new UserServiceDto(userSql);
        userService.deleteProfilePic(userServiceDto);

    }

    protected void createProfileDictionaryCql(UserCql userCql) {

        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(userCql.getProfile_path());
        if (profileDictionaryCql == null) {
            profileDictionaryCql = new ProfileDictionaryCql();
            profileDictionaryCql.setProfile_path(userCql.getProfile_path());
            profileDictionaryCql.setUser_email(userCql.getEmail());
            profileDictionaryRepository.save(profileDictionaryCql);
        }

    }
    
}
