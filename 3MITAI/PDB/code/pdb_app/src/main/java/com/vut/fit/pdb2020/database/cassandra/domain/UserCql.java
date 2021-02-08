package com.vut.fit.pdb2020.database.cassandra.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Table("user")
public class UserCql {
    @PrimaryKey
    private String email;

    private String name;

    private String surname;

    private String password_hash;

    @Column("profile_path")
    private String profilePath;

    private String profile_photo_path;

    private Instant last_active;

    private List<Long> owned_pages;

    private Boolean status;

    private Instant created_at;

    public UserCql() {}

    public UserCql(UserSql userSql) {
        this.email = userSql.getEmail();
        this.name = userSql.getName();
        this.surname = userSql.getSurname();
        this.password_hash = userSql.getPassword_hash();
        this.profilePath = userSql.getProfilePath();
        this.profile_photo_path = userSql.getProfilePhotoPath();
        this.created_at = userSql.getCreated_at();
    }

    public UserCql(String email, String name, String surname, String password_hash, String profile_path, String profile_photo_path, Instant last_active, List<Long> owned_pages, Boolean status, Instant created_at) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password_hash = password_hash;
        this.profilePath = profile_path;
        this.profile_photo_path = profile_photo_path;
        this.last_active = last_active;
        this.owned_pages = owned_pages;
        this.status = status;
        this.created_at = created_at;
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

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getProfile_path() {
        return profilePath;
    }

    public void setProfile_path(String profile_path) {
        this.profilePath = profile_path;
    }

    public String getProfile_photo_path() {
        return profile_photo_path;
    }

    public void setProfile_photo_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }

    public Instant getLast_active() {
        return last_active;
    }

    public void setLast_active(Instant last_active) {
        this.last_active = last_active;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public List<Long> getOwned_pages() {
        return owned_pages;
    }

    public void setOwned_pages(List<Long> owned_pages) {
        this.owned_pages = owned_pages;
    }
}
