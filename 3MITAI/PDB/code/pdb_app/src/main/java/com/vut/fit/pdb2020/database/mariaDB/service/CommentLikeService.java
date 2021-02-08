package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.CommentLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface CommentLikeService {

    void raiseEvent(CommentLikeDto commentLikeDto);

}

@Service
class CommentLikeServiceImpl implements CommentLikeService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    public void raiseEvent(CommentLikeDto dto) {
        try{
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("comment-like-service-event", dto.getId(), value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}