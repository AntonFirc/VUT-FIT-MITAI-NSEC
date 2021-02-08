package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import com.vut.fit.gja2020.app.models.StudentGroupConnector;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentGroupConnectorRepository extends CrudRepository<StudentGroupConnector, String> {

    List<StudentGroupConnector> findAllByStudent(Student student);

    List<StudentGroupConnector> findAllByGroup(ProjectGroup group);

    StudentGroupConnector findByStudentAndGroup(Student student, ProjectGroup group);

}
