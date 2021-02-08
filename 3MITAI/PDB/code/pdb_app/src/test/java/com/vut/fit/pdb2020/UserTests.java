package com.vut.fit.pdb2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.UserCreateDto;
import com.vut.fit.pdb2020.database.dto.UserDetailDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    private UserCreateDto userCreateDto;

    private String userProfileSlug;


    @BeforeAll
    public void init() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Peter");
        userCreateDto.setSurname("Sveter");
        userCreateDto.setGender("M");
        userCreateDto.setEmail("peter.sveter@email.cz");
        userCreateDto.setAddress("Božetěchova 2");
        userCreateDto.setCity("Brno");
        userCreateDto.setPassword("111111");
    }

    @Test
    @Order(1)
    public void createState() throws Exception {

        MvcResult result = mvc.perform(post("/state/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
                        new BasicNameValuePair("name", "Absurdistan")
                ))))).andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        userCreateDto.setStateId(Long.parseLong(result.getResponse().getContentAsString()));

    }

    @Test
    @Order(2)
    public void createUser() throws Exception {

        String userJson = jsonObjectMapper.writeValueAsString(this.userCreateDto);

        MvcResult result = mvc.perform( post("/user/create")
                .content(userJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isOk()).andReturn();

        assertThat(NumberUtils.isCreatable(result.getResponse().getContentAsString())).isTrue();

        String resultString = result.getResponse().getContentAsString();

        this.userProfileSlug = String.format("%s%s.%s",
                userCreateDto.getName(),
                userCreateDto.getSurname(),
                resultString);

    }

    @Test
    @Order(3)
    public void getUserProfile() throws Exception {

        MvcResult result = mvc.perform(
                get("/user/{profileSlug}", this.userProfileSlug).accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailDto userDetail = jsonObjectMapper.readValue(result.getResponse().getContentAsString(), UserDetailDto.class);

        assertThat(userDetail.getName().equals("Peter")).isTrue();
    }

    @Test
    @Order(4)
    public void login() throws Exception {

        mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userCreateDto.getEmail()),
                        new BasicNameValuePair("password", userCreateDto.getPassword())
                ))))).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void logout() throws Exception {

        mvc.perform(post("/user/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userCreateDto.getEmail())
                ))))).andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void getUsers() throws Exception {

        MvcResult result = mvc.perform(
                get("/users/active").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString.contains(userCreateDto.getEmail())).isTrue();

    }

    @Test
    @Order(7)
    public void deleteUser() throws Exception {

        mvc.perform(post("/user/delete")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", userCreateDto.getEmail())
                ))))).andExpect(status().isOk());

    }

    @Test
    @Order(8)
    public void deleteState() throws Exception {

        mvc.perform(post("/state/delete")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("stateId", userCreateDto.getStateId().toString())
                ))))).andExpect(status().isOk());

    }

}
