package com.vut.fit.pdb2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.UserCreateDto;
import com.vut.fit.pdb2020.database.dto.UserDetailDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.ProfileDictionarySql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.WallSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.ProfileDictionarySqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.WallSqlRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscriptionTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    UserSql userSql1;
    UserSql userSql2;
    UserCql userCql1;
    UserCql userCql2;
    PageSql pageSql;
    PageCql pageCql;
    WallSql wall1;
    WallSql wall2;
    WallSql wall3;
    ProfileDictionarySql profile1;
    ProfileDictionarySql profile2;
    ProfileDictionarySql profile3;
    ProfileDictionaryCql profileCql1;
    ProfileDictionaryCql profileCql2;
    ProfileDictionaryCql profileCql3;

    @BeforeAll
    public void init() {

        wall1 = new WallSql();
        wall1 = wallSqlRepository.save(wall1);
        wall2 = new WallSql();
        wall2 = wallSqlRepository.save(wall2);

        userSql1 = new UserSql();
        userSql1.setEmail("user1@nonexist");
        userSql1.setName("blank");
        userSql1.setSurname("user");
        userSql1.setPassword_hash("pass");
        userSql1.setGender("M");
        userSql1.setWall(wall1);
        userSqlRepository.save(userSql1);

        userSql2 = new UserSql();
        userSql2.setEmail("user2@nonexist");
        userSql2.setName("empty");
        userSql2.setSurname("useress");
        userSql2.setPassword_hash("pass");
        userSql2.setGender("F");
        userSql2.setWall(wall2);
        userSqlRepository.save(userSql2);

        userCql1 = new UserCql();
        userCql1.setEmail("user1@nonexist");
        userCql1.setProfile_path(userSql1.getProfilePath());
        userCql1 = userRepository.save(userCql1);

        userCql2 = new UserCql();
        userCql2.setEmail("user2@nonexist");
        userCql2.setProfile_path(userSql2.getProfilePath());
        userCql2 = userRepository.save(userCql2);

        wall3 = new WallSql();
        wall3 = wallSqlRepository.save(wall3);

        pageSql = new PageSql();
        pageSql.setName("blank");
        pageSql.setAdmin(userSql1);
        pageSql.setWall(wall3);
        pageSqlRepository.save(pageSql);

        pageCql = new PageCql();
        pageCql.setName("blank");
        pageCql.setId(pageSql.getId());
        pageCql.setProfile_path(pageSql.getProfilePath());
        pageRepository.save(pageCql);

        profile1 = new ProfileDictionarySql();
        profile1.setUser(userSql1);
        profile1.setPath(userSql1.getProfilePath());
        profile1 = profileDictionarySqlRepository.save(profile1);

        profile2 = new ProfileDictionarySql();
        profile2.setUser(userSql2);
        profile2.setPath(userSql2.getProfilePath());
        profile2 = profileDictionarySqlRepository.save(profile2);

        profile3 = new ProfileDictionarySql();
        profile3.setPage(pageSql);
        profile3.setPath(pageSql.getProfilePath());
        profile3 = profileDictionarySqlRepository.save(profile3);

        profileCql1 = new ProfileDictionaryCql();
        profileCql1.setProfile_path(userSql1.getProfilePath());
        profileCql1.setUser_email(userSql1.getEmail());
        profileDictionaryRepository.save(profileCql1);

        profileCql2 = new ProfileDictionaryCql();
        profileCql2.setProfile_path(userSql2.getProfilePath());
        profileCql2.setUser_email(userSql2.getEmail());
        profileDictionaryRepository.save(profileCql2);

        profileCql3 = new ProfileDictionaryCql();
        profileCql3.setProfile_path(pageSql.getProfilePath());
        profileCql3.setPage_id(pageSql.getId());
        profileDictionaryRepository.save(profileCql3);
    }

    @Test
    @Order(1)
    public void subscribeToUser() throws Exception {

        MvcResult result = mvc.perform(post("/subscribe/user").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "user1@nonexist"),
                        new BasicNameValuePair("subscribes", "user2@nonexist")
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        Thread.sleep(2000);

    }

    @Test
    @Order(2)
    public void subscribeToPage() throws Exception {
        MvcResult result = mvc.perform(post("/subscribe/page")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "user1@nonexist"),
                        new BasicNameValuePair("subscribes", pageCql.getId().toString())
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        Thread.sleep(2000);
    }

    @Test
    @Order(3)
    public void getSubscribers() throws Exception {
        String profileSlug = String.format("%s%s.%d", userSql2.getName(), userSql2.getSurname(), userSql2.getId()).toLowerCase();

        MvcResult result = mvc.perform(get("/subscribers/user/{slug}", profileSlug)
                ).andExpect(status().isOk()).andReturn();


        String resultString = result.getResponse().getContentAsString();

        assertThat(resultString.contains(String.format("%s %s", userCql1.getName(), userCql1.getSurname()))).isTrue();
        assertThat(resultString.contains(String.format("%s %s", userCql2.getName(), userCql2.getSurname()))).isTrue();
    }

    @Test
    @Order(4)
    public void getPageSubscribers() throws Exception {

        String profileSlug = String.format("%s.%d", pageSql.getName(), pageSql.getId()).toLowerCase();

        MvcResult result = mvc.perform(get("/subscribers/page/{slug}", profileSlug)
                ).andExpect(status().isOk()).andReturn();

        String resultString = result.getResponse().getContentAsString();

        assertThat(resultString.contains(String.format("%s %s", userCql1.getName(), userCql1.getSurname()))).isTrue();
        assertThat(resultString.contains(pageCql.getName())).isTrue();

    }

    @Test
    @Order(5)
    public void getSubscribedTo() throws Exception {
        String profileSlug = String.format("%s%s.%d", userSql1.getName(), userSql1.getSurname(), userSql1.getId()).toLowerCase();

        MvcResult result = mvc.perform(get("/subscribed-to/{slug}", profileSlug)
                ).andExpect(status().isOk()).andReturn();

        String resultString = result.getResponse().getContentAsString();

        assertThat(resultString.contains(String.format("%s %s", userCql2.getName(), userCql2.getSurname()))).isTrue();
        assertThat(resultString.contains(pageCql.getName())).isTrue();
    }

    @Test
    @Order(6)
    public void unsubscribeUser() throws Exception {
        mvc.perform(post("/unsubscribe/user")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("userEmail", "user1@nonexist"),
                        new BasicNameValuePair("unsubscribeFromEmail", "user2@nonexist")
                ))))).andExpect(status().isOk());

    }

    @Test
    @Order(7)
    public void unsubscribePage() throws Exception {
       mvc.perform(post("/unsubscribe/page")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "user1@nonexist"),
                        new BasicNameValuePair("pageId", pageCql.getId().toString())
                ))))).andExpect(status().isOk());

    }

    @AfterAll
    public void cleanUp() {
        profileDictionarySqlRepository.delete(profile1);
        profileDictionarySqlRepository.delete(profile2);
        profileDictionarySqlRepository.delete(profile3);

        profileDictionaryRepository.deleteByPath(userSql1.getProfilePath());
        profileDictionaryRepository.deleteByPath(userSql2.getProfilePath());
        profileDictionaryRepository.deleteByPath(pageSql.getProfilePath());

        pageSql.setDeleted(true);
        pageSqlRepository.save(pageSql);
        pageRepository.deleteById(pageCql.getId());
        userSql1 = userSqlRepository.findByEmail(userSql1.getEmail());
        userSql1.setDeleted(true);
        userSqlRepository.save(userSql1);
        userSql2 = userSqlRepository.findByEmail(userSql2.getEmail());
        userSql2.setDeleted(true);
        userSqlRepository.save(userSql2);
        userRepository.deleteByEmail(userCql1.getEmail());
        userRepository.deleteByEmail(userCql2.getEmail());
        wall1.setDeleted(true);
        wallSqlRepository.save(wall1);
        wall2.setDeleted(true);
        wallSqlRepository.save(wall2);
        wall3.setDeleted(true);
        wallSqlRepository.save(wall3);
    }

}
