package com.vut.fit.pdb2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.*;
import com.vut.fit.pdb2020.database.cassandra.repository.*;
import com.vut.fit.pdb2020.database.dto.GroupCreateDto;
import com.vut.fit.pdb2020.database.dto.MessageSendDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserChatRepository userChatRepository;

    @Autowired
    UserChatSqlRepository userChatSqlRepository;

    @Autowired
    ChatSqlRepository chatSqlRepository;

    @Autowired
    MessageSqlRepository messageSqlRepository;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatUserRepository chatUserRepository;

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    UserSql userSql1;
    UserSql userSql2;
    UserCql userCql1;
    UserCql userCql2;
    WallSql wall1;
    WallSql wall2;
    ProfileDictionarySql profile1;
    ProfileDictionarySql profile2;
    ProfileDictionaryCql profileCql1;
    ProfileDictionaryCql profileCql2;

    @BeforeAll
    public void init() {

        wall1 = new WallSql();
        wall1 = wallSqlRepository.save(wall1);
        wall2 = new WallSql();
        wall2 = wallSqlRepository.save(wall2);

        userSql1 = new UserSql();
        userSql1.setEmail("user1@nonemail");
        userSql1.setName("blank");
        userSql1.setSurname("user");
        userSql1.setPassword_hash("pass");
        userSql1.setGender("M");
        userSql1.setWall(wall1);
        userSql1 = userSqlRepository.save(userSql1);

        userSql2 = new UserSql();
        userSql2.setEmail("user2@nonemail");
        userSql2.setName("empty");
        userSql2.setSurname("useress");
        userSql2.setPassword_hash("pass");
        userSql2.setGender("F");
        userSql2.setWall(wall2);
        userSql2 = userSqlRepository.save(userSql2);

        userCql1 = new UserCql();
        userCql1.setEmail("user1@nonemail");
        userCql1.setProfile_path(userSql1.getProfilePath());
        userCql1 = userRepository.save(userCql1);

        userCql2 = new UserCql();
        userCql2.setEmail("user2@nonemail");
        userCql2.setProfile_path(userSql2.getProfilePath());
        userCql2 = userRepository.save(userCql2);

        profile1 = new ProfileDictionarySql();
        profile1.setUser(userSql1);
        profile1.setPath(userSql1.getProfilePath());
        profile1 = profileDictionarySqlRepository.save(profile1);

        profile2 = new ProfileDictionarySql();
        profile2.setUser(userSql2);
        profile2.setPath(userSql2.getProfilePath());
        profile2 = profileDictionarySqlRepository.save(profile2);

        profileCql1 = new ProfileDictionaryCql();
        profileCql1.setProfile_path(userSql1.getProfilePath());
        profileCql1.setUser_email(userSql1.getEmail());
        profileDictionaryRepository.save(profileCql1);

        profileCql2 = new ProfileDictionaryCql();
        profileCql2.setProfile_path(userSql2.getProfilePath());
        profileCql2.setUser_email(userSql2.getEmail());
        profileDictionaryRepository.save(profileCql2);
    }

    @Test
    @Order(1)
    public void sendMessageToUser() throws Exception {

        MessageSendDto messageDto = new MessageSendDto();
        messageDto.setContent("no message");
        messageDto.setAuthor("user1@nonemail");
        messageDto.setReceiver("user2@nonemail");

        String messageJson = jsonObjectMapper.writeValueAsString(messageDto);

        MvcResult result = mvc.perform(post("/chats/messages/send")
                .content(messageJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString().equals("Message send")).isTrue();

        Thread.sleep(2000);

    }

    @Test
    @Order(2)
    public void createGroup() throws Exception {

        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setName("test group");

        String groupJson = jsonObjectMapper.writeValueAsString(groupCreateDto);

        MvcResult result = mvc.perform(post("/chats/groups/create")
                .content(groupJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        Thread.sleep(2000);

    }

    @Test
    @Order(3)
    public void getChats() throws Exception {

        String profileSlug = String.format("%s%s.%d", userSql1.getName(), userSql1.getSurname(), userSql1.getId()).toLowerCase();

        MvcResult result = mvc.perform(get("/chats/{slug}", profileSlug)
        ).andExpect(status().isOk()).andReturn();

        String resultString = result.getResponse().getContentAsString();

        assertThat(resultString.contains("test group")).isFalse();

    }

    @AfterAll
    public void cleanUp() {
        profileDictionarySqlRepository.delete(profile1);
        profileDictionarySqlRepository.delete(profile2);

        profileDictionaryRepository.deleteByPath(userSql1.getProfilePath());
        profileDictionaryRepository.deleteByPath(userSql2.getProfilePath());

        userSql1 = userSqlRepository.findByEmail(userSql1.getEmail());

        List<UserChatSql> userChatSql1List = userChatSqlRepository.findAllByUser(userSql1);

        for (UserChatSql userChatSql:
             userChatSql1List) {

            ChatSql chatSql = userChatSql.getChat();
            List<MessageSql> messageSqlList = chatSql.getMessages();

//            for (MessageSql messageSql:
//                    messageSqlList) {
//
//                messageSqlRepository.delete(messageSql);
//            }

            userChatSqlRepository.delete(userChatSql);

            // delete CQL
            chatRepository.deleteById(chatSql.getId());
            chatMessageRepository.deleteAllByChatId(chatSql.getId());

            List<ChatUserCql> chatUserCqlList = chatUserRepository.findAllByChatId(chatSql.getId());

            for (ChatUserCql chatUserCql:
                    chatUserCqlList) {

                UserChatCql userChatCql = userChatRepository.findByUserEmailAndChatId(chatUserCql.getUserEmail(), chatUserCql.getChatId());
                userChatRepository.delete(userChatCql);
                chatUserRepository.delete(chatUserCql);
            }


            //chatSqlRepository.delete(chatSql);
        }

        userSql1.setDeleted(true);
        userSqlRepository.save(userSql1);
        userSql2 = userSqlRepository.findByEmail(userSql2.getEmail());

        List<UserChatSql> userChatSql2List = userChatSqlRepository.findAllByUser(userSql1);

        for (UserChatSql userChatSql:
                userChatSql2List) {

            ChatSql chatSql = userChatSql.getChat();
            List<MessageSql> messageSqlList = chatSql.getMessages();

//            for (MessageSql messageSql:
//                 messageSqlList) {
//
//                messageSqlRepository.delete(messageSql);
//            }

            userChatSqlRepository.delete(userChatSql);

            // delete CQL
            chatRepository.deleteById(chatSql.getId());
            chatMessageRepository.deleteAllByChatId(chatSql.getId());

            List<ChatUserCql> chatUserCqlList = chatUserRepository.findAllByChatId(chatSql.getId());

            for (ChatUserCql chatUserCql:
                 chatUserCqlList) {

                UserChatCql userChatCql = userChatRepository.findByUserEmailAndChatId(chatUserCql.getUserEmail(), chatUserCql.getChatId());
                userChatRepository.delete(userChatCql);
                chatUserRepository.delete(chatUserCql);
            }

            //chatSqlRepository.delete(chatSql);
        }

        userSql2.setDeleted(true);
        userSqlRepository.save(userSql2);
        userRepository.deleteByEmail(userCql1.getEmail());
        userRepository.deleteByEmail(userCql2.getEmail());
        wall1.setDeleted(true);
        wallSqlRepository.save(wall1);
        wall2.setDeleted(true);
        wallSqlRepository.save(wall2);

        ChatSql testGroup = chatSqlRepository.findByName("test group");

        ChatCql testGroupCql = chatRepository.findById(testGroup.getId());

        chatRepository.delete(testGroupCql);
        chatSqlRepository.delete(testGroup);
    }
}
