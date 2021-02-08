package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.dto.ProjectDetailDto;
import com.vut.fit.gja2020.app.dto.ProjectGroupDto;
import com.vut.fit.gja2020.app.dto.StudentListDto;
import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.repository.*;
import com.vut.fit.gja2020.app.utils.MailingUtility;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

@Component
@ManagedBean
@ApplicationScoped
public class Dashboard {

    @Autowired
    MailingUtility mailer;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ProjectGroupRepository projectGroupRepository;

    @Autowired
    StudentGroupConnectorRepository sgConnectorRepository;

    @Autowired
    StudentCourseRepository studentCourseRepository;

    private DashboardModel model;

    private List<Course> courseList;
    private List<Student> studentList;
    private List<ProjectGroup> groupList;


    @PostConstruct
    public void init() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();

        column1.addWidget("courses");
        column1.addWidget("smtp");
        column2.addWidget("students");
        column2.addWidget("mailing");
        column3.addWidget("groups");
        column3.addWidget("accounts");

        model.addColumn(column1);
        model.addColumn(column2);
        model.addColumn(column3);

    }

    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

        addMessage(message);
    }

    public void handleClose(CloseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed", "Closed panel id:'" + event.getComponent().getId() + "'");

        addMessage(message);
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled", "Status:" + event.getVisibility().name());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }

    public List<Course> getCourseList() {
        if (this.courseList == null || this.courseList.size() == 0)
            this.courseList = courseRepository.findAll();
        return this.courseList;
    }

    public List<StudentListDto> getStudentList() {
        if (this.studentList == null || this.studentList.size() == 0)
            this.studentList = studentRepository.findAll();

        return this.studentList.stream().map(item -> new StudentListDto(item, studentCourseRepository)).collect(Collectors.toList());
    }

    public List<ProjectDetailDto> getGroupList() {
        if (this.groupList == null || this.groupList.size() == 0)
            this.groupList = projectGroupRepository.findAll();

        return this.groupList.stream().map(item -> new ProjectDetailDto(item, sgConnectorRepository)).collect(Collectors.toList());
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void setGroupList(List<ProjectGroup> groupList) {
        this.groupList = groupList;
    }
}
