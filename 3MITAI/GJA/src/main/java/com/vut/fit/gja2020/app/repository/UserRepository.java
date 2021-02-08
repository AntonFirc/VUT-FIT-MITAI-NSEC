package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Student, String> {

    Optional<Student> findByLogin(String login);

}
