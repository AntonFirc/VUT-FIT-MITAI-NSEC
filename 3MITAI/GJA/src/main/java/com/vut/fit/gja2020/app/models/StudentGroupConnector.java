package com.vut.fit.gja2020.app.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class StudentGroupConnector implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @OneToOne
    @JoinColumn(name = "groupId")
    private ProjectGroup group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ProjectGroup getGroup() {
        return group;
    }

    public void setGroup(ProjectGroup group) {
        this.group = group;
    }
}
