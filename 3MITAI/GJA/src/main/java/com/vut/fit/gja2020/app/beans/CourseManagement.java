package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.StudentCourseConnector;
import com.vut.fit.gja2020.app.repository.CourseRepository;
import com.vut.fit.gja2020.app.repository.ProjectGroupRepository;
import com.vut.fit.gja2020.app.repository.StudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
@ManagedBean
@ViewScoped
public class CourseManagement {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ProjectGroupRepository projectGroupRepository;

    @Autowired
    StudentCourseRepository studentCourseRepository;

    @Autowired
    Dashboard dashboard;

    private String name;
    private Integer year;
    private String groupDirectoryPath = "/opt/groups/";

    public List<Course> getCourses() {
        return courseRepository.findAllByYear(Calendar.getInstance().get(Calendar.YEAR));
    }

    public List<Long> getCourseIds() {
        List<Course> courses = courseRepository.findAll();
        List<Long> courseIds = new ArrayList<>(courses.size());
        for (Course course : courses) {
            courseIds.add(course.getId());
        }

        return courseIds;
    }

    public List<String> getCourseList() {
        List<Course> courses = courseRepository.findAll();
        List<String> courseStrings = new ArrayList<>(courses.size());
        for (Course course : courses) {
            courseStrings.add(course.toString());
        }

        return courseStrings;
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
        if (!groupDirectoryPath.substring(groupDirectoryPath.length() - 1).equals("/")) {
            groupDirectoryPath = groupDirectoryPath.concat("/");
        }
        this.groupDirectoryPath = groupDirectoryPath;
    }

    /**
     * Creates new course and stores it in database
     *
     * ViewParams (taken from inputs)
     *  - String name
     *  - String groupDirectoryPath (optional - generated by default)
     */
    public void addCourse() {
        assert this.name != null;

        String directory = this.groupDirectoryPath;
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (directory == null || directory.isEmpty()) {
            directory = String.format("%s%s-%d/",groupDirectoryPath, this.name, currentYear);
        }
        else {
            directory = directory.concat(String.format("%s-%d/", this.name, currentYear));
        }

        Course course = new Course();
        course.setName(this.name);
        course.setYear(currentYear);
        course.setGroupDirectoryPath(directory);
        courseRepository.save(course);
        dashboard.setCourseList(null);

        FacesMessage message = new FacesMessage("Úspěch", "předmět vytvořen");
        FacesContext.getCurrentInstance().addMessage(null, message);

    }

    /**
     * Removes a course from database and delete folder for projects directories.
     * Course can be removed only if no project groups are assigned to the given course.
     *
     * @param id - id of course to be removed
     * @throws IOException
     * @throws InterruptedException
     */
    public void removeCourse(Long id) throws IOException, InterruptedException {

        Course course = courseRepository.findById(id);
        assert course != null;

        List<StudentCourseConnector> connectors = studentCourseRepository.findAllByCourse(course);
        if (connectors != null && !connectors.isEmpty()) {
            studentCourseRepository.deleteAll(connectors);
        }

        List<ProjectGroup> groups = projectGroupRepository.findAllByCourse(course);
        assert groups == null;

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(String.format("sudo rm -rf %s", course.getGroupDirectoryPath()));
        proc.waitFor();

        courseRepository.delete(course);
        dashboard.setCourseList(null);

        FacesMessage message = new FacesMessage("Úspěch", "předmět odstraněn");
        FacesContext.getCurrentInstance().addMessage(null, message);

    }

    /**
     * Remove all courses from system.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void removeAllCourses() throws IOException, InterruptedException {

        List<Course> courses = courseRepository.findAll();

        for (Course course : courses) {
            this.removeCourse(course.getId());
        }

    }
}