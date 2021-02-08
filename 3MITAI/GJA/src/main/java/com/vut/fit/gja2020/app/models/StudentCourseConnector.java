package com.vut.fit.gja2020.app.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class StudentCourseConnector implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @OneToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private boolean receivedInfo = false;

    public StudentCourseConnector() {}

    public StudentCourseConnector(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean isReceivedInfo() {
        return receivedInfo;
    }

    public void setReceivedInfo(boolean receivedInfo) {
        this.receivedInfo = receivedInfo;
    }
}
