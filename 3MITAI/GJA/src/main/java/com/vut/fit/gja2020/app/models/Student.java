package com.vut.fit.gja2020.app.models;

import org.apache.commons.text.RandomStringGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Student implements Serializable
{

    // Persistent Fields:
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String login;
    private Integer uid;
    private String password;
    private String homeDirectory;

    // Constructors:
    public Student() {}

    // generates a random string to be used as password
    protected String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(97, 122)
                .build();
        return pwdGenerator.generate(length);
    }

    public Student(String name, String login, Integer uid) {
        this.name = name;
        this.login = login;
        this.uid = uid;
        this.password = generateRandomSpecialCharacters(10);
    }

    // String Representation:
    @Override
    public String toString() {
        return this.name + "/" + this.password + "/" + this.login + "/" + this.uid;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getLogin() { return this.login; }
    public void setLogin(String login) { this.login = login; }

    public Integer getUid() { return this.uid; }
    public void setUid(Integer uid) { this.uid = uid; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }
}
