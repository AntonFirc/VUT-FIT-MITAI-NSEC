package com.vut.fit.gja2020.app.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class IpAddress implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String ipAddress;

    @OneToOne
    @JoinColumn(name = "studentId")
    private Student student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
