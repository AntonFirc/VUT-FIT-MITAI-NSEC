package com.vut.fit.gja2020.app.utils;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentGroupConnector;
import com.vut.fit.gja2020.app.repository.CourseRepository;
import com.vut.fit.gja2020.app.repository.ProjectGroupRepository;
import com.vut.fit.gja2020.app.repository.StudentGroupConnectorRepository;
import com.vut.fit.gja2020.app.repository.StudentRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
public class HtmlParser {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ProjectGroupRepository projectGroupRepository;

    @Autowired
    StudentGroupConnectorRepository sgConnectorRepository;

    public String osGroupNameTemplate = "pGrp_%d";

    /**
     * Imports project groups from HTML file and store them to database.
     * @param file
     * @param course
     * @throws Exception
     */
    public void importGroups(UploadedFile file, Course course) throws Exception {

        String content = new String(file.getContent());

        Document doc = Jsoup.parse(content);

        int index = 0;

        //Fetch table by unique attribute combination
        Elements tableRows = doc.select("table[cellpadding=0][cellspacing=0][border=1][bordercolor=#808080][bgcolor=#dddddd][width=100%] tr");
        for (Element tr : tableRows) {
            if (index == 0) {
                index++;
                continue;
            }

            Elements rowData = tr.getElementsByTag("td");
            int dataIdx = 0;

            Student student = null;
            String leaderLogin = null;
            Long groupId = null;
            String groupName = null;

            for (Element td : rowData) {
                switch (dataIdx) {
                    case 2:
                        String login = td.html();
                        if (!Pattern.matches("^x[a-z][a-z][a-z][a-z][a-z][0-9][0-9]$", login))
                            throw new Exception(String.format("Invalid login format: \"%s\"", login));
                        student = studentRepository.findByLogin(login);
                        if (student == null)
                            throw new Exception(String.format("Student with login %s is not enrolled within this course.", login));
                        break;
                    case 7:
                        Elements anchors = td.children();
                        Element anchor = anchors.get(0);

                        String[] groupLink = anchor.attr("href").split("/?id=");
                        groupId = Long.parseLong(groupLink[groupLink.length-1]);
                        groupName = URLEncoder.encode(anchor.html(), StandardCharsets.UTF_8.toString());
                        break;
                    case 8:
                        if (!td.html().equals("&nbsp;")) {
                            assert student != null;
                            leaderLogin = student.getLogin();
                        }
                        break;
                    default:
                        break;
                }
                dataIdx++;
            }

            assert groupId != null && groupName != null;

            ProjectGroup projectGroup = projectGroupRepository.findById(groupId);

            if (projectGroup == null) {
                projectGroup = new ProjectGroup();
                projectGroup.setId(groupId);
                projectGroup.setName(groupName);
                projectGroup.setCourse(course);
                String osGroupName = String.format(osGroupNameTemplate, groupId);
                projectGroup.setOsGroupName(osGroupName);
                projectGroup.setWorkDirectory(course.getGroupDirectoryPath().concat(String.format("%s/temp", osGroupName)));
                projectGroup.setSubmitDirectory(course.getGroupDirectoryPath().concat(String.format("%s/submit", osGroupName)));
                if (leaderLogin != null)
                    projectGroup.setLeaderLogin(leaderLogin);
            }
            else if(leaderLogin != null) {
                projectGroup.setLeaderLogin(leaderLogin);
            }

            projectGroup = projectGroupRepository.save(projectGroup);

            List<StudentGroupConnector> allStudentConnectors = sgConnectorRepository.findAllByStudent(student);
            List<StudentGroupConnector> relevantGroupConnectors =  allStudentConnectors.stream().filter(item -> item.getGroup().getCourse().getId().equals(course.getId())).collect(Collectors.toList());
            if (relevantGroupConnectors.size() != 0) {
                for (StudentGroupConnector connector : relevantGroupConnectors) {
                    Runtime runtime = Runtime.getRuntime();
                    Process proc = runtime.exec(String.format("sudo gpasswd -d %s %s", connector.getStudent().getLogin(), connector.getGroup().getOsGroupName()));
                    proc.waitFor();
                    sgConnectorRepository.delete(connector);
                }
            }

            StudentGroupConnector connector = sgConnectorRepository.findByStudentAndGroup(student, projectGroup);

            if (connector == null) {
                connector = new StudentGroupConnector();
                connector.setGroup(projectGroup);
                connector.setStudent(student);
                sgConnectorRepository.save(connector);
            }

            index++;
        }


    }

}
