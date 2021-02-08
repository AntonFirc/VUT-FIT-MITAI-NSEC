package com.vut.fit.gja2020.app.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Course implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer year;
    private String groupDirectoryPath;

    public String toString() {
        return String.format("%s-%d : %s", this.name, this.year, this.groupDirectoryPath);
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGroupDirectoryPath() {
        return groupDirectoryPath;
    }

    public void setGroupDirectoryPath(String groupDirectoryPath) {
        this.groupDirectoryPath = groupDirectoryPath;
    }
}
