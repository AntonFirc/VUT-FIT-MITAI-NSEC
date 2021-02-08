package com.vut.fit.pdb2020.database.cassandra.domain;


import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.beans.Transient;
import java.time.Instant;

@Table("page")
public class PageCql {

    @PrimaryKey
    private Long id;

    private String name;

    private String admin_email;

    private String profile_path;

    private String profile_photo_path;

    private Instant last_active;

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

    public String getAdmin_email() {
        return admin_email;
    }

    public void setAdmin_email(String admin_email) {
        this.admin_email = admin_email;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public Instant getLast_active() {
        return last_active;
    }

    public void setLast_active(Instant last_active) {
        this.last_active = last_active;
    }

    public String getProfile_photo_path() {
        return profile_photo_path;
    }

    public void setProfile_photo_path(String profile_photo_path) {
        this.profile_photo_path = profile_photo_path;
    }
}
