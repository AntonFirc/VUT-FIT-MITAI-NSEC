package com.vut.fit.pdb2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PageTests {

    @Autowired
    private MockMvc mvc;

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
    UserCql userCql1;
    WallSql wall1;
    ProfileDictionarySql profile1;
    ProfileDictionaryCql profileCql1;

    Long pageId;

    @BeforeAll
    public void init() {

        wall1 = new WallSql();
        wall1 = wallSqlRepository.save(wall1);

        userSql1 = new UserSql();
        userSql1.setEmail("user1@nonexist");
        userSql1.setName("blank");
        userSql1.setSurname("user");
        userSql1.setPassword_hash("pass");
        userSql1.setGender("M");
        userSql1.setWall(wall1);
        userSqlRepository.save(userSql1);

        userCql1 = new UserCql();
        userCql1.setEmail("user1@nonexist");
        userCql1.setProfile_path(userSql1.getProfilePath());
        userCql1 = userRepository.save(userCql1);

        profile1 = new ProfileDictionarySql();
        profile1.setUser(userSql1);
        profile1.setPath(userSql1.getProfilePath());
        profile1 = profileDictionarySqlRepository.save(profile1);

        profileCql1 = new ProfileDictionaryCql();
        profileCql1.setProfile_path(userSql1.getProfilePath());
        profileCql1.setUser_email(userSql1.getEmail());
        profileDictionaryRepository.save(profileCql1);

    }

    @Test
    @Order(1)
    public void createPage() throws Exception {

        MvcResult result = mvc.perform(post("/page/create").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userSql1.getEmail()),
                        new BasicNameValuePair("name", "TestingPage")
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        pageId = Long.parseLong(result.getResponse().getContentAsString());

        Thread.sleep(4000);
    }

    @Test
    @Order(2)
    public void getPageProfile() throws Exception {

        String profileSlug = String.format("%s.%d", "TestingPage", pageId).toLowerCase();

        mvc.perform(
                get("/page/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk());

    }

    @Test
    @Order(3)
    public void checkUserPages() throws Exception {

        String profileSlug = String.format("%s%s.%d", userSql1.getName(), userSql1.getSurname(), userSql1.getId()).toLowerCase();

        MvcResult result = mvc.perform(
                get("/user/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();

        JSONObject jsonObject = new JSONObject(resultString);
        JSONArray pages = jsonObject.getJSONArray("ownedPages");
        assertThat(pages.length()).isEqualTo(1);
        assertThat(pages.get(0)).isEqualTo(pageId.intValue());
    }

    @Test
    @Order(4)
    public void deletePage() throws Exception {

       mvc.perform(post("/page/delete").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
                        new BasicNameValuePair("id", pageId.toString())
                ))))).andExpect(status().isOk());

    }

    @AfterAll
    public void cleanUp() {
        profileDictionarySqlRepository.delete(profile1);

        profileDictionaryRepository.deleteByPath(userSql1.getProfilePath());

        userSql1 = userSqlRepository.findByEmail(userSql1.getEmail());
        userSql1.setDeleted(true);
        userSqlRepository.save(userSql1);

        userRepository.deleteByEmail(userCql1.getEmail());
        wall1.setDeleted(true);
        wallSqlRepository.save(wall1);
    }

}
