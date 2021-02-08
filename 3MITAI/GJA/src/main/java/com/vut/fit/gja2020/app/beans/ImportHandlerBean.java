package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.repository.CourseRepository;
import com.vut.fit.gja2020.app.repository.ProjectGroupRepository;
import com.vut.fit.gja2020.app.repository.StudentRepository;
import com.vut.fit.gja2020.app.utils.*;
import org.primefaces.event.FileUploadEvent;


import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

@Component
@ManagedBean
@ViewScoped
public class ImportHandlerBean {

    private UploadedFile uFile;

    private UploadedFile htmlFile;

    private Course studentCourse;

    private Course groupCourse;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectGroupRepository projectGroupRepository;

    @Autowired
    CsvReader csvReader;

    @Autowired
    HtmlParser htmlParser;

    @Autowired
    UserAccountUtility userAcUtil;

    @Autowired
    FirewallUtility firewallUtility;

    private String delimiter;
    private String secondaryDelimiter;
    private String partEnclosing;

    private int nameIdx;
    private int loginIdx;
    private int uidIdx;

    public ImportHandlerBean() {}

    @PostConstruct
    public void init() {
        delimiter = csvReader.delimiter;
        secondaryDelimiter = csvReader.secondaryDelimiter;
        partEnclosing = csvReader.partEnclosing;
        nameIdx = csvReader.nameIdx;
        loginIdx = csvReader.loginIdx;
        uidIdx = csvReader.uidIdx;
    }

    public Long getStudentCourse() {
        if (studentCourse != null)
            return studentCourse.getId();
        return null;
    }

    public void setStudentCourse(Long courseId) {
        this.studentCourse = courseRepository.findById(courseId);
    }

    public Long getGroupCourse() {
        if (groupCourse != null)
            return groupCourse.getId();
        return null;
    }

    public void setGroupCourse(Long courseId) {
        this.groupCourse = courseRepository.findById(courseId);
    }


    public UploadedFile getFile() {
        return uFile;
    }

    public void setFile(UploadedFile file) {
        this.uFile = file;
    }

    public UploadedFile getGroups() {return this.htmlFile;}

    public void setGroups(UploadedFile file) {this.htmlFile = file;}

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        csvReader.delimiter = delimiter;
    }

    public String getSecondaryDelimiter() {
        return secondaryDelimiter;
    }

    public void setSecondaryDelimiter(String secondaryDelimiter) {

        csvReader.secondaryDelimiter = secondaryDelimiter;
        this.secondaryDelimiter = secondaryDelimiter;
    }

    public String getPartEnclosing() {
        return partEnclosing;
    }

    public void setPartEnclosing(String partEnclosing) {

        csvReader.partEnclosing = partEnclosing;
        this.partEnclosing = partEnclosing;
    }

    public int getNameIdx() {
        return nameIdx;
    }

    public void setNameIdx(int nameIdx) {
        csvReader.nameIdx = nameIdx;
        this.nameIdx = nameIdx;
    }

    public int getLoginIdx() {
        csvReader.loginIdx = loginIdx;
        return loginIdx;
    }

    public void setLoginIdx(int loginIdx) {
        this.loginIdx = loginIdx;
    }

    public int getUidIdx() {
        return uidIdx;
    }

    public void setUidIdx(int uidIdx) {
        csvReader.uidIdx = uidIdx;
        this.uidIdx = uidIdx;
    }

    public void upload() throws Exception {
        if (uFile != null) {
            FacesMessage message = new FacesMessage("Úspěch", uFile.getFileName() + " importováno.");
            FacesContext.getCurrentInstance().addMessage(null, message);

            csvReader.importStudents(uFile, studentCourse);
        }

    }

    public void importHtmlGroups() throws Exception {
        if (htmlFile != null) {
            FacesMessage message = new FacesMessage("Úspěch", htmlFile.getFileName() + " importováno.");
            FacesContext.getCurrentInstance().addMessage(null, message);

            htmlParser.importGroups(htmlFile, groupCourse);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Úspěch", event.getFile().getFileName() + " importováno.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<String> getStudentList() {
        List<Student> persistedStudents = studentRepository.findAll();
        List<String> studentStrings = new ArrayList<String>(persistedStudents.size());
        for (Student student : persistedStudents) {
            studentStrings.add(student.toString());
        }

        return studentStrings;
    }

    public List<String> getGroupList() {
        List<ProjectGroup> groupList = projectGroupRepository.findAll();
        List<String> groupStrings = new ArrayList<String>(groupList.size());
        for (ProjectGroup projectGroup : groupList) {
            groupStrings.add(projectGroup.toString());
        }

        return groupStrings;
    }

}
