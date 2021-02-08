package com.vut.fit.pdb2020.database.dto.converter;

import com.vut.fit.pdb2020.database.cassandra.domain.ChatCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ChatUserCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserChatCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.ChatUserRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserChatRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.UserChatDto;
import com.vut.fit.pdb2020.database.dto.UserDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatDtoConverter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDtoConverter userDtoConverter;

    @Autowired
    UserChatRepository userChatRepository;

    @Autowired
    ChatUserRepository chatUserRepository;

    @Autowired
    UserSqlRepository userSqlRepository;

    public UserChatDto cqlToBasic(ChatCql chatCql, String email) {

        UserChatDto userChatDto = null;

        if(chatCql != null) {

            userChatDto = new UserChatDto();

            userChatDto.setName(chatCql.getName());
            userChatDto.setId(chatCql.getId());

            UserDto userDto = null;

            UserCql userCql = userRepository.findByEmail(email);

            if(userCql != null) {
                userDto = userDtoConverter.userCqlToBasic(userCql);
            } else {

                UserSql userSql = userSqlRepository.findByEmail(email);
                userDto = userDtoConverter.userSqlToBasic(userSql);
            }

            userChatDto.setUser(userDto);

            List<ChatUserCql> chatUserCqlList = null;
            chatUserCqlList = chatUserRepository.findAllByChatId(chatCql.getId());

            List<UserDto> participantsList = new ArrayList<>();

            for (ChatUserCql chatUserCql:
                 chatUserCqlList) {

                if(!chatUserCql.getUserEmail().equals(email)) {

                    UserCql participantCql = userRepository.findByEmail(chatUserCql.getUserEmail());
                    UserDto participantDto = userDtoConverter.userCqlToBasic(participantCql);

                    participantsList.add(participantDto);
                }
            }

            userChatDto.setUsers(participantsList);
        }

        return userChatDto;
    }
}
