package com.vut.fit.pdb2020.database.dto.converter;

import com.vut.fit.pdb2020.database.cassandra.domain.ChatMessageCql;
import com.vut.fit.pdb2020.database.dto.MessageDto;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoConverter {

    public MessageDto cqlToDto(ChatMessageCql messageCql) {

        MessageDto messageDto = null;

        if(messageCql != null) {

            messageDto = new MessageDto();

            messageDto.setContent(messageCql.getMessage());
            messageDto.setTime(messageCql.getCreatedAt());
            messageDto.setAuthor(messageCql.getUser());
        }

        return messageDto;
    }
}
