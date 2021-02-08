package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PostCommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface PostCommentService {

    void raiseEvent(PostCommentDto postCommentDto);

}

@Service
class PostCommentServiceImpl implements PostCommentService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;


    public void raiseEvent(PostCommentDto dto){
        try{
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("comment-service-event", dto.getId(), value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}