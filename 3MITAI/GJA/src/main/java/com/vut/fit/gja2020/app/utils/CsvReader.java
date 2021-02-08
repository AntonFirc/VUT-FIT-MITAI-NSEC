package com.vut.fit.gja2020.app.utils;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentCourseConnector;
import com.vut.fit.gja2020.app.repository.StudentCourseRepository;
import com.vut.fit.gja2020.app.repository.StudentRepository;


import org.apache.commons.text.StringEscapeUtils;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CsvReader {

    private static final String DEFAULT_DELIMITER = ",";
    private static final String DEFAULT_SECONDARY_DELIMITER = " ";
    private static final String DEFAULT_ITEM_ENCLOSING = "\"";

    private static final int DEFAULT_NAME_IDX = 0;
    private static final int DEFAULT_LOGIN_IDX = 1;
    private static final int DEFAULT_UID_IDX = 2;

    public String delimiter;
    public String secondaryDelimiter;
    public String partEnclosing;

    public int nameIdx;
    public int loginIdx;
    public int uidIdx;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    public CsvReader() {
        this.delimiter = DEFAULT_DELIMITER;
        this.secondaryDelimiter = DEFAULT_SECONDARY_DELIMITER;
        this.nameIdx = DEFAULT_NAME_IDX;
        this.loginIdx = DEFAULT_LOGIN_IDX;
        this.uidIdx = DEFAULT_UID_IDX;
        this.partEnclosing = DEFAULT_ITEM_ENCLOSING;
    }

    /**
     * Correctly escapes meta characters, so they dont mess with strings from CSV files
     *
     * @param inputString
     * @return
     */
    private String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, "\\" + metaCharacter);
            }
        }
        return inputString;
    }

    /**
     * Imports students from CSV file and stores their info in database. Also generates password for each student.
     *
     * @param file
     * @throws IOException
     */
    public void importStudents(UploadedFile file, Course course) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        List<Student> persistedStudents = studentRepository.findAll();
        List<Student> importedStudents = new ArrayList<>();
        List<StudentCourseConnector> studentsCourses = new ArrayList<>();

        while(reader.ready()) {

            String line = reader.readLine();

            String[] parsedLine = line.split(this.escapeMetaCharacters(this.delimiter));

            String login = parsedLine[this.loginIdx];

            if (!Pattern.matches("^x[a-z][a-z][a-z][a-z][a-z][0-9][0-9]$", login))
                throw new Exception(String.format("Invalid login format: \"%s\"", login));

            Student student = persistedStudents.stream()
                    .filter(perStudent -> login.equals(perStudent.getLogin()))
                    .findAny()
                    .orElse(null);

            if (student != null) {
                StudentCourseConnector persistedCourse = studentCourseRepository.findByStudentAndCourse(student, course);
                if (persistedCourse == null) {
                    studentCourseRepository.save(new StudentCourseConnector(student, course)); }
                continue;
            }

            String name = parsedLine[this.nameIdx];
            Integer uid = Integer.parseInt(parsedLine[this.uidIdx]);

            student = new Student(name, login, uid);
            importedStudents.add(student);
            studentsCourses.add(new StudentCourseConnector(student, course));
        }

        importedStudents.forEach(studentRepository::save);
        studentsCourses.forEach(studentCourseRepository::save);

    }

}