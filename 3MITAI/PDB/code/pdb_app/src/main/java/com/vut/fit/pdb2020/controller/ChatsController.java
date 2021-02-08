package com.vut.fit.pdb2020.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.*;
import com.vut.fit.pdb2020.database.cassandra.repository.*;
import com.vut.fit.pdb2020.database.dto.*;
import com.vut.fit.pdb2020.database.dto.converter.ChatDtoConverter;
import com.vut.fit.pdb2020.database.dto.converter.MessageDtoConverter;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatsController {

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserChatRepository userChatRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSqlRepository chatSqlRepository;

    @Autowired
    private UserSqlRepository userSqlRepository;

    @Autowired
    private UserChatSqlRepository userChatSqlRepository;

    @Autowired
    private ProfileDictionarySqlRepository profileDictionarySqlRepository;

    @Autowired
    private MessageSqlRepository messageSqlRepository;

    @Autowired
    private ChatDtoConverter chatDtoConverter;

    @Autowired
    private MessageDtoConverter messageDtoConverter;

    @Transactional
    @GetMapping("/chats/{profileSlug}")
    public List<UserChatDto> getChats(@PathVariable("profileSlug") String profileSlug) throws Exception {

        assert profileSlug != null;

        String profilePath = "/user/".concat(profileSlug);

        String userEmail;

        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);

        if (profileDictionaryCql != null) {
            userEmail = profileDictionaryCql.getUser_email();
        }
        else {
            ProfileDictionarySql profileDictionary = profileDictionarySqlRepository.findByPath(profilePath);
            userEmail = profileDictionary.getUser().getEmail();
        }

        List<UserChatDto> userChatDtos = new ArrayList<>();

        List<UserChatCql> userChatConnectors = userChatRepository.findAllByUserEmail(userEmail);

        List<ChatCql> chatCqlList = new ArrayList<>();
        for (UserChatCql connector:
                userChatConnectors) {

            chatCqlList.add(chatRepository.findById(connector.getChatId()));
        }

        for (ChatCql chatCql:
                chatCqlList) {

            UserChatDto chatDto = chatDtoConverter.cqlToBasic(chatCql, userEmail);
            userChatDtos.add(chatDto);
        }

        return userChatDtos;
    }

    @Transactional
    @GetMapping("/chats/messages/{id}")
    public List<MessageDto> getMessages(@PathVariable("id") Long id) {

        List<ChatMessageCql> chatMessageCqlList = chatMessageRepository.findAllByChatId(id);

        // Donacitanie sprav /more
        List<MessageDto> messageDtoList = new ArrayList<>();
        for (ChatMessageCql messageCql:
             chatMessageCqlList) {

            messageDtoList.add(messageDtoConverter.cqlToDto(messageCql));
        }

        return messageDtoList;

    }

    @Transactional
    @PostMapping("/chats/groups/create")
    public String createGroup(@RequestBody String groupJson) throws Exception {

        GroupCreateDto groupCreateDto = jsonObjectMapper.readValue(groupJson, GroupCreateDto.class);

        Instant time = Instant.now();

        Long id = null;

        try {

            ChatSql chatSql = new ChatSql();
            chatSql.setName(groupCreateDto.getName());
            chatSql.setDeleted(false);
            chatSql.setCreated_at(time);
            chatSql.setUpdated_at(time);

            chatSql = chatSqlRepository.save(chatSql);

            ChatCql chatCql = new ChatCql();
            chatCql.setId(chatSql.getId());
            chatCql.setUpdatedAt(time);
            chatCql.setName(groupCreateDto.getName());

            chatRepository.save(chatCql);

            id = chatCql.getId();
        }
        catch (Exception e) {
            return e.toString();
        }

        return id.toString();
    }

    @Transactional
    @PostMapping("/chats/groups/add")
    public String addToGroup(@RequestBody String addGroupJson) throws Exception {

        GroupAddDto groupAddDto = jsonObjectMapper.readValue(addGroupJson, GroupAddDto.class);

        Instant time = Instant.now();

        try {

            UserSql userSql = userSqlRepository.findByEmail(groupAddDto.getEmail());
            ChatSql chatSql = chatSqlRepository.findById(groupAddDto.getGroup());

            if(chatSql == null || userSql == null) {
                throw new Exception("User or chat not found!");
            }

            UserChatSql userChatSql = userChatSqlRepository.findByUserAndChat(userSql, chatSql);

            if(userChatSql == null) {

                userChatSql = new UserChatSql();
                userChatSql.setUser(userSql);
                userChatSql.setChat(chatSql);
                userChatSql.setDeleted(false);
                userChatSql.setCreated_at(time);
                userChatSql.setUpdated_at(time);

                userChatSql = userChatSqlRepository.save(userChatSql);

                // Connect users to chat
                UserChatCql userChatCql = new UserChatCql();
                userChatCql.setChatId(userChatSql.getId());
                userChatCql.setUserEmail(userSql.getEmail());
                userChatCql.setFrom(time);

                // Both sides
                ChatUserCql chatUserCql = new ChatUserCql();
                chatUserCql.setChatId(userChatSql.getId());
                chatUserCql.setUserEmail(userSql.getEmail());
                chatUserCql.setFrom(time);

                userChatRepository.save(userChatCql);
                chatUserRepository.save(chatUserCql);
            } else {
                throw new Exception("User is already added to group!");
            }
        }
        catch (Exception e) {
            return e.toString();
        }

        return "User added to group";
    }

    @Transactional
    @PostMapping("/chats/messages/send")
    public String sendMessage(@RequestBody String messageJson) throws Exception {

        MessageSendDto messageSendDto = jsonObjectMapper.readValue(messageJson, MessageSendDto.class);

        UserSql author = userSqlRepository.findByEmail(messageSendDto.getAuthor());

        Instant time = Instant.now();

        if(author == null) {
            throw new Exception("Author not found!");
        }

        // Direct user to user messages
        if(!messageSendDto.getReceiver().equals("")) {

            UserSql receiver = userSqlRepository.findByEmail(messageSendDto.getReceiver());
            if(receiver == null) {
                throw new Exception("Receiver not found!");
            }

            List<UserChatCql> authorChats = userChatRepository.findAllByUserEmail(author.getEmail());
            List<UserChatCql> receiverChats = userChatRepository.findAllByUserEmail(receiver.getEmail());

            // optimize?
            Long chatId = null;
            boolean found = false;

            for (UserChatCql authorChatCql:
                 authorChats) {

                for (UserChatCql receiverChatCql:
                     receiverChats) {

                    if(authorChatCql.getChatId().equals(receiverChatCql.getChatId())) {

                        // check if chat is between 2 users
                        Long id = authorChatCql.getChatId();
                        List<ChatUserCql> chatUserCqlList = chatUserRepository.findAllByChatId(id);
                        if(chatUserCqlList.size() == 2) {

                            chatId = id;
                            found = true;
                            break;
                        }
                    }
                }

                if(found) {
                    break;
                }
            }
            // end

            if(!found) {

                // Create new chat

                ChatSql chatSql = new ChatSql();
                chatSql.setName(null);
                chatSql.setCreated_at(time);
                chatSql.setUpdated_at(time);
                chatSql.setDeleted(false);

                try {
                    chatSql = chatSqlRepository.save(chatSql);
                }
                catch (Exception e) {
                    return e.toString();
                }

                ChatCql chatCql = new ChatCql();
                chatCql.setName(null);
                chatCql.setUpdatedAt(chatSql.getUpdated_at());
                chatCql.setId(chatSql.getId());

                try {
                    chatCql = chatRepository.save(chatCql);
                }
                catch (Exception e) {
                    return e.toString();
                }

                chatId = chatCql.getId();

                // SQL part
                UserChatSql userChatSql1 = new UserChatSql();
                userChatSql1.setChat(chatSql);
                userChatSql1.setUser(author);
                userChatSql1.setCreated_at(time);
                userChatSql1.setDeleted(false);
                userChatSql1.setUpdated_at(time);

                UserChatSql userChatSql2 = new UserChatSql();
                userChatSql2.setChat(chatSql);
                userChatSql2.setUser(receiver);
                userChatSql2.setCreated_at(time);
                userChatSql2.setDeleted(false);
                userChatSql2.setUpdated_at(time);

                // Connect users to chat
                UserChatCql userChatCql1 = new UserChatCql();
                userChatCql1.setChatId(chatId);
                userChatCql1.setUserEmail(author.getEmail());
                userChatCql1.setFrom(time);

                UserChatCql userChatCql2 = new UserChatCql();
                userChatCql2.setChatId(chatId);
                userChatCql2.setUserEmail(receiver.getEmail());
                userChatCql2.setFrom(time);

                //Both sides
                ChatUserCql chatUserCql1 = new ChatUserCql();
                chatUserCql1.setChatId(chatId);
                chatUserCql1.setUserEmail(author.getEmail());
                chatUserCql1.setFrom(time);

                ChatUserCql chatUserCql2 = new ChatUserCql();
                chatUserCql2.setChatId(chatId);
                chatUserCql2.setUserEmail(receiver.getEmail());
                chatUserCql2.setFrom(time);

                try {
                    userChatSqlRepository.save(userChatSql1);
                    userChatSqlRepository.save(userChatSql2);

                    userChatRepository.save(userChatCql1);
                    userChatRepository.save(userChatCql2);

                    chatUserRepository.save(chatUserCql1);
                    chatUserRepository.save(chatUserCql2);
                }
                catch (Exception e) {
                    return e.toString();
                }
            }

            try {
                ChatMessageCql chatMessageCql = new ChatMessageCql();

                chatMessageCql.setChatId(chatId);
                chatMessageCql.setCreatedAt(Instant.now());
                chatMessageCql.setMessage(messageSendDto.getContent());
                chatMessageCql.setUser(author.getEmail());

                chatMessageRepository.save(chatMessageCql);

                MessageSql messageSql = new MessageSql();

                messageSql.setAuthor(author);

                ChatSql chatSql = chatSqlRepository.findById(chatId);
                messageSql.setChat(chatSql);
                messageSql.setCreatedAt(time);
                messageSql.setUpdated_at(time);
                messageSql.setDeleted(false);
                messageSql.setContent(messageSendDto.getContent());

                messageSqlRepository.save(messageSql);

            }
            catch (Exception e) {
                return e.toString();
            }
        } else if(messageSendDto.getChatId() != null) {

            Long chatId = messageSendDto.getChatId();

            ChatCql chatCql = chatRepository.findById(chatId);

            if(chatCql == null) {
                throw new Exception("Group not found!");
            }

            try {
                ChatMessageCql chatMessageCql = new ChatMessageCql();

                chatMessageCql.setChatId(chatId);
                chatMessageCql.setCreatedAt(Instant.now());
                chatMessageCql.setMessage(messageSendDto.getContent());
                chatMessageCql.setUser(author.getEmail());

                chatMessageRepository.save(chatMessageCql);

                MessageSql messageSql = new MessageSql();

                messageSql.setAuthor(author);

                ChatSql chatSql = chatSqlRepository.findById(chatId);
                messageSql.setChat(chatSql);
                messageSql.setCreatedAt(time);
                messageSql.setUpdated_at(time);
                messageSql.setDeleted(false);
                messageSql.setContent(messageSendDto.getContent());

                messageSqlRepository.save(messageSql);

            }
            catch (Exception e) {
                return e.toString();
            }
        }

        return "Message send";
    }
}
