package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.Course;
import com.vut.fit.gja2020.app.models.ProjectGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectGroupRepository extends CrudRepository<ProjectGroup, String> {

    List<ProjectGroup> findAll();

    List<ProjectGroup> findByName(String name);

    ProjectGroup findById(long id);

    List<ProjectGroup> findAllByCourse(Course course);

}
