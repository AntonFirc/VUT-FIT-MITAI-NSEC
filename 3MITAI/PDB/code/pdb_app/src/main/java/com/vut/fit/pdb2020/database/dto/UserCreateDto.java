package com.vut.fit.pdb2020.database.dto;

public class UserCreateDto {

    private String email;

    private String name;

    private String surname;

    private String password;

    private String gender;

    private String address;

    private String city;

    private Long stateId;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long state) {
        this.stateId = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password_hash) {
        this.password = password_hash;
    }

}
