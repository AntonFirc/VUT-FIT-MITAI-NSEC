package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.*;
import com.vut.fit.pdb2020.database.cassandra.repository.*;
import com.vut.fit.pdb2020.database.dto.PostDto;
import com.vut.fit.pdb2020.database.dto.UserServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

public interface UserServiceEventHandler {

    void updateUser(UserServiceDto userServiceDto);

    void changePassword(UserServiceDto userServiceDto);

    void deleteUser(UserServiceDto userServiceDto);

    void addProfilePic(UserServiceDto userServiceDto);

    void deleteProfilePic(UserServiceDto userServiceDto);

}

@Service
class UserServiceEventHandlerImpl implements UserServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileDictionaryRepository profileDictionaryRepository;


    @KafkaListener(topics = "user-service-event")
    public void consume(String userStr) {
        try{
            UserServiceDto user = OBJECT_MAPPER.readValue(userStr, UserServiceDto.class);
            if (user.isDelete()) {
                if (user.isPhoto()) {
                    this.deleteProfilePic(user);
                    return;
                }
                this.deleteUser(user);
                return;
            }
            if (user.isPhoto()) {
                this.addProfilePic(user);
                return;
            }
            if (user.isPassword()) {
                this.changePassword(user);
            }
            else {
                this.updateUser(user);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateUser(UserServiceDto userServiceDto) {

        UserCql user = userRepository.findByEmail(userServiceDto.getEmail());
        assert user != null;

        ProfileDictionaryCql dict = profileDictionaryRepository.findByPath(user.getProfile_path());

        user.setName(userServiceDto.getName());
        user.setSurname(userServiceDto.getSurname());
        user.setProfile_path(userServiceDto.getProfilePath());
        user.setProfile_photo_path(userServiceDto.getProfilePhotoPath());
        user.setLast_active(Instant.now());
        userRepository.save(user);

        dict.setProfile_path(user.getProfile_path());
        profileDictionaryRepository.save(dict);

    }

    public void changePassword(UserServiceDto userServiceDto) {

        UserCql user = userRepository.findByEmail(userServiceDto.getEmail());
        assert user != null;

        user.setPassword_hash(userServiceDto.getPassword_hash());
        userRepository.save(user);

    }

    public void deleteUser(UserServiceDto userServiceDto) {
        userRepository.deleteByEmail(userServiceDto.getEmail());
    }

    public void addProfilePic(UserServiceDto userServiceDto) {

        UserCql userCql = userRepository.findByEmail(userServiceDto.getEmail());

        assert userCql != null;

        userCql.setProfile_photo_path(userServiceDto.getProfilePhotoPath());
        userCql.setLast_active(Instant.now());
        userRepository.save(userCql);

    }

    public void deleteProfilePic(UserServiceDto userServiceDto) {

        UserCql userCql = userRepository.findByEmail(userServiceDto.getEmail());

        assert userCql != null;

        userCql.setProfile_photo_path(null);
        userRepository.save(userCql);

    }

}
