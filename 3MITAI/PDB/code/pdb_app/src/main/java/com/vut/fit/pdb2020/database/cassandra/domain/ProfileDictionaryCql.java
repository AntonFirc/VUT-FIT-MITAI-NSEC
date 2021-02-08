package com.vut.fit.pdb2020.database.cassandra.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("profile_link_dictionary")
public class ProfileDictionaryCql {

    @PrimaryKey
    private String path;

    private Long page_id;

    private String user_email;

    public String getProfile_path() {
        return path;
    }

    public void setProfile_path(String profile_path) {
        this.path = profile_path;
    }

    public Long getPage_id() {
        return page_id;
    }

    public void setPage_id(Long page_id) {
        this.page_id = page_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
