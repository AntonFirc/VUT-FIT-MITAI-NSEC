package com.vut.fit.pdb2020.database.dto;

public class NameProfileTuple {

    private String name;
    private String profileLink;

    public NameProfileTuple(String name, String profileLink) {
        this.name = name;
        this.profileLink = profileLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }
}
