package com.vut.fit.gja2020.app.beans;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentCourseConnector;
import com.vut.fit.gja2020.app.repository.CourseRepository;
import com.vut.fit.gja2020.app.repository.StudentCourseRepository;
import com.vut.fit.gja2020.app.utils.MailingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import java.util.List;

@Component
@ManagedBean
@ViewScoped
public class Mailer {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentCourseRepository studentCourseRepository;

    @Autowired
    MailingUtility mailer;

    private Course course;

    private String server;

    private String lectures;

    private String email;

    private String template;

    private String subject;

    private static final String LOGIN_SLUG = "\\{login}";
    private static final String PASSWORD_SLUG = "\\{password}";
    private static final String COURSE_NAME_SLUG = "\\{course}";
    private static final String HOME_DIR_SLUG = "\\{home}";
    private static final String GROUP_DIR_SLUG = "\\{group}";
    private static final String SERVER_SLUG = "\\{server}";
    private static final String LECTURES_SLUG = "\\{lectures}";
    private static final String EMAIL_SLUG = "\\{email}";

    /**
     * Constructor, sets default template for mailing
     */
    public Mailer() {
        template = "Informace k predmetu {course}\n" +
                "\n" +
                "Vase prihlasovaci udaje na server {server} jsou:\n" +
                "login: {login}\n" +
                "heslo: {password}\n" +
                "\n" +
                "Prezentace z prednasek, priklady apod. naleznete v adresari {lectures}\n" +
                "Individualni projekty si muzete testovat ve svem domovskem adresari {home}\n" +
                "Adresare pro tymove projekty Vam po prihlaseni na projekt vytvorime v adresari {group}\n" +
                "\n" +
                "Na tento e-mail prosim neodpovidejte - pripadne dotazy piste na {email}";
    }

    public Long getCourse() {
        if (course != null)
            return course.getId();
        return null;
    }

    public void setCourse(Long courseId) {
        this.course = courseRepository.findById(courseId);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLectures() {
        return lectures;
    }

    public void setLectures(String lectures) {
        this.lectures = lectures;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Fetch all students enrolled in selected course and email them info, if they already have not received it.
     *
     * @throws MessagingException
     */
    public void sendInfo() throws MessagingException {

        String customTemplate = template.replaceAll(SERVER_SLUG, server)
                            .replaceAll(LECTURES_SLUG, lectures)
                            .replaceAll(EMAIL_SLUG, email)
                            .replaceAll(COURSE_NAME_SLUG, course.getName());

        List<StudentCourseConnector> connectors = studentCourseRepository.findAllByCourse(course);

        for (StudentCourseConnector connector : connectors) {
            Student student = connector.getStudent();
            if (!connector.isReceivedInfo()) {
                String content = customTemplate.replaceAll(LOGIN_SLUG, student.getLogin())
                        .replaceAll(PASSWORD_SLUG, student.getPassword())
                        .replaceAll(HOME_DIR_SLUG, student.getHomeDirectory())
                        .replaceAll(GROUP_DIR_SLUG, course.getGroupDirectoryPath());

                String mail = String.format("%s@stud.fit.vutbr.cz", student.getLogin());
                String mailSubject = (subject == null) ? "Prihlasovaci udaje na vyukovy server" : subject;

                mailer.sendMail(mail, mailSubject, content);
                connector.setReceivedInfo(true);
                studentCourseRepository.save(connector);
            }
        }

        FacesMessage message = new FacesMessage("Úspěch", "info zasláno");
        FacesContext.getCurrentInstance().addMessage(null, message);

    }


}
