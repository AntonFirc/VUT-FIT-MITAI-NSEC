package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PageDto;
import com.vut.fit.pdb2020.database.dto.UserServiceDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

public interface UserService {

    void updateUser(UserServiceDto userServiceDto);

    void changePassword(UserServiceDto userServiceDto);

    void deleteUser(UserServiceDto userServiceDto);

    void addProfilePic(UserServiceDto userServiceDto);

    void deleteProfilePic(UserServiceDto userServiceDto);

}

@Service
class UserServiceImpl implements UserService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    UserPageSqlRepository userPageSqlRepository;

    @Autowired
    PhotoSqlRepository photoSqlRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    public void updateUser(UserServiceDto userServiceDto) {
        this.raiseEvent(userServiceDto);
    }

    public void changePassword(UserServiceDto userServiceDto) {
        userServiceDto.setPassword(true);
        this.raiseEvent(userServiceDto);
    }

    public void deleteUser(UserServiceDto userServiceDto) {
        userServiceDto.setDelete(true);
        this.raiseEvent(userServiceDto);
    }

    public void addProfilePic(UserServiceDto userServiceDto) {
        userServiceDto.setDelete(false);
        userServiceDto.setPhoto(true);
        this.raiseEvent(userServiceDto);
    }

    public void deleteProfilePic(UserServiceDto userServiceDto) {
        userServiceDto.setDelete(true);
        userServiceDto.setPhoto(true);
        this.raiseEvent(userServiceDto);
    }

    private void raiseEvent(UserServiceDto dto) {
        try {
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("user-service-event", dto.getId(), value);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}