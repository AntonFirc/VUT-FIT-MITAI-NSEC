package com.vut.fit.pdb2020.database.dto;

public class UserDto {

    private String name;

    private String surname;

    private String profilePath;

    private String profilePhotoPath;

    private Boolean status;

    public UserDto(String name, String surname, String profilePath, String profilePhotoPath, Boolean status) {
        this.name = name;
        this.surname = surname;
        this.profilePath = profilePath;
        this.profilePhotoPath = profilePhotoPath;
        this.status = status;
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

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
