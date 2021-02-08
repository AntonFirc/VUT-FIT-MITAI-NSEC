package com.vut.fit.pdb2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostTests {

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
    UserCql userCql1;
    PageSql pageSql;
    PageCql pageCql;
    WallSql wall1;
    WallSql wall3;
    ProfileDictionarySql profile1;
    ProfileDictionarySql profile3;
    ProfileDictionaryCql profileCql1;
    ProfileDictionaryCql profileCql3;

    private String userPostCreatedAt;
    private String pagePostCreatedAt;

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

        profile3 = new ProfileDictionarySql();
        profile3.setPage(pageSql);
        profile3.setPath(pageSql.getProfilePath());
        profile3 = profileDictionarySqlRepository.save(profile3);

        profileCql1 = new ProfileDictionaryCql();
        profileCql1.setProfile_path(userSql1.getProfilePath());
        profileCql1.setUser_email(userSql1.getEmail());
        profileDictionaryRepository.save(profileCql1);

        profileCql3 = new ProfileDictionaryCql();
        profileCql3.setProfile_path(pageSql.getProfilePath());
        profileCql3.setPage_id(pageSql.getId());
        profileDictionaryRepository.save(profileCql3);
    }

    @Test
    @Order(1)
    public void postAsUser() throws Exception {

        MvcResult result = mvc.perform(post("/user/post/create").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userSql1.getEmail()),
                        new BasicNameValuePair("contentType", "text"),
                        new BasicNameValuePair("textContent", "Test post")
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

    }

    @Test
    @Order(2)
    public void postAsPage() throws Exception {

        MvcResult result = mvc.perform(post("/page/post/create").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("pageId", pageSql.getId().toString()),
                        new BasicNameValuePair("contentType", "text"),
                        new BasicNameValuePair("textContent", "Test page post")
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        Thread.sleep(2000);
    }

    @Test
    @Order(3)
    public void getUserProfilePost() throws Exception {

        String profileSlug = String.format("%s%s.%d", userSql1.getName(), userSql1.getSurname(), userSql1.getId()).toLowerCase();

        MvcResult result = mvc.perform(
                get("/user/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString.contains("Test post")).isTrue();

        JSONObject jsonObject = new JSONObject(resultString);
        JSONArray posts = jsonObject.getJSONArray("posts");
        JSONObject post = posts.getJSONObject(0);
        userPostCreatedAt = post.getString("createdAt");
    }

    @Test
    @Order(4)
    public void getPageProfilePosts() throws Exception {

        String profileSlug = String.format("%s.%d", pageSql.getName(), pageSql.getId()).toLowerCase();

        MvcResult result = mvc.perform(
                get("/page/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString.contains("Test page post")).isTrue();

        JSONObject jsonObject = new JSONObject(resultString);
        JSONArray posts = jsonObject.getJSONArray("posts");
        JSONObject post = posts.getJSONObject(0);
        pagePostCreatedAt = post.getString("createdAt");
    }

    @Test
    @Order(5)
    public void deleteUserPost() throws Exception {

        mvc.perform(post("/user/post/delete").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userSql1.getEmail()),
                        new BasicNameValuePair("contentType", "text"),
                        new BasicNameValuePair("createdAt", userPostCreatedAt)
                ))))).andExpect(status().isOk());


    }

    @Test
    @Order(6)
    public void deletePagePost() throws Exception {

       mvc.perform(post("/page/post/delete").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("pageId", pageSql.getId().toString()),
                        new BasicNameValuePair("contentType", "text"),
                        new BasicNameValuePair("createdAt", pagePostCreatedAt)
                ))))).andExpect(status().isOk());


    }

    @Test
    @Order(7)
    public void checkUserPostDeleted() throws Exception {

        String profileSlug = String.format("%s%s.%d", userSql1.getName(), userSql1.getSurname(), userSql1.getId()).toLowerCase();

        MvcResult result = mvc.perform(
                get("/user/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString.contains("Test post")).isFalse();

    }

    @Test
    @Order(8)
    public void checkPagePostDeleted() throws Exception {

        String profileSlug = String.format("%s.%d", pageSql.getName(), pageSql.getId()).toLowerCase();

        MvcResult result = mvc.perform(
                get("/page/{profileSlug}", profileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString.contains("Test page post")).isFalse();

        Thread.sleep(2000);
    }

    @AfterAll
    public void cleanUp() {
        profileDictionarySqlRepository.delete(profile1);
        profileDictionarySqlRepository.delete(profile3);

        profileDictionaryRepository.deleteByPath(userSql1.getProfilePath());
        profileDictionaryRepository.deleteByPath(pageSql.getProfilePath());

        pageSql.setDeleted(true);
        pageSqlRepository.save(pageSql);
        pageRepository.deleteById(pageCql.getId());
        userSql1 = userSqlRepository.findByEmail(userSql1.getEmail());
        userSql1.setDeleted(true);
        userSqlRepository.save(userSql1);

        userRepository.deleteByEmail(userCql1.getEmail());
        wall1.setDeleted(true);
        wallSqlRepository.save(wall1);
        wall3.setDeleted(true);
        wallSqlRepository.save(wall3);
    }

}
