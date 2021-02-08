package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PostLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface PostLikeService {

    void raiseEvent(PostLikeDto postLikeDto);

}

@Service
class PostLikeServiceImpl implements PostLikeService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;


    public void raiseEvent(PostLikeDto dto){
        try{
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("like-service-event", dto.getId(), value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}