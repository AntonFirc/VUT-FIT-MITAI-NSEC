package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentCourseConnector;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentCourseRepository extends CrudRepository<StudentCourseConnector, String> {

    List<StudentCourseConnector> findAllByStudent(Student student);

    List<StudentCourseConnector> findAllByCourse(Course course);

    StudentCourseConnector findByStudentAndCourse(Student student, Course course);

}
