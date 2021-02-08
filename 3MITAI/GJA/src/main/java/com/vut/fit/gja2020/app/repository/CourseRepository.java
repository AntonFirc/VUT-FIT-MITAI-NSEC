package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, String> {

    List<Course> findAll();

    Course findByName(String name);

    Course findById(Long id);

    List<Course> findAllByYear(Integer year);

}
