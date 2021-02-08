package com.vut.fit.pdb2020.database.dto;

import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;

public class UserServiceDto {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private String password_hash;

    private String profilePhotoPath;

    private String profilePath;

    private String createdAt;

    private boolean password;

    private boolean delete;

    private boolean isPhoto;

    public UserServiceDto() {    }

    public UserServiceDto(UserSql user) {
        id = user.getId();
        name = user.getName();
        surname = user.getSurname();
        email = user.getEmail();
        password_hash = user.getPassword_hash();
        profilePath = user.getProfilePath();
        profilePhotoPath = user.getProfilePhotoPath();
        createdAt = user.getCreated_at().toString();
        password = false;
        delete = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setPhoto(boolean photo) {
        isPhoto = photo;
    }
}
