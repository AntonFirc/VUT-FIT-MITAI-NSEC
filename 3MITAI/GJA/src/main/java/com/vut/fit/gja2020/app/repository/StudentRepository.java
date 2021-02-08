package com.vut.fit.gja2020.app.repository;

import java.util.List;
import java.util.Optional;

import com.vut.fit.gja2020.app.models.ProjectGroup;
import com.vut.fit.gja2020.app.models.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, String> {

    List<Student> findAll();

    List<Student> findByName(String name);

    Student findById(long id);

    Student findByLogin(String login);

}
