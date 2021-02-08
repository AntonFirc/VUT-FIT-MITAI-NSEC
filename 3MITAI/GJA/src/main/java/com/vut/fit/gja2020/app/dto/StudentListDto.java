package com.vut.fit.gja2020.app.dto;

import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentCourseConnector;
import com.vut.fit.gja2020.app.repository.StudentCourseRepository;

import java.util.List;

public class StudentListDto {

    private String name;
    private String login;
    private String password;
    private String receivedInfo;
    private String courses;

    public StudentListDto() {}

    public StudentListDto(Student student, StudentCourseRepository studentCourseRepository) {
        name = student.getName();
        login = student.getLogin();
        password = student.getPassword();
        receivedInfo = "-";
        List<StudentCourseConnector> connectors = studentCourseRepository.findAllByStudent(student);

        courses = "";

        for (StudentCourseConnector connector : connectors) {
            if (connector.isReceivedInfo()) {
                receivedInfo = receivedInfo.concat(String.format("%s ", connector.getCourse().getName()));
            }
            courses = courses.concat(String.format("%s ", connector.getCourse().getName()));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReceivedInfo() {
        return receivedInfo;
    }

    public void setReceivedInfo(String receivedInfo) {
        this.receivedInfo = receivedInfo;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }
}
