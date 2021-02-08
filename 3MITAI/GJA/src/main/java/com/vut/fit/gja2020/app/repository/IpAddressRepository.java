package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.IpAddress;
import com.vut.fit.gja2020.app.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IpAddressRepository extends CrudRepository<IpAddress, String> {

    List<IpAddress> findAll();

    List<IpAddress> findAllByStudent(Student student);

}
