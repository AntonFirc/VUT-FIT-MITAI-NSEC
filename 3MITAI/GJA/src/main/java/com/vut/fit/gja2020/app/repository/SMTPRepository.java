package com.vut.fit.gja2020.app.repository;

import com.vut.fit.gja2020.app.models.SMTPSettings;
import org.springframework.data.repository.CrudRepository;

public interface SMTPRepository extends CrudRepository<SMTPSettings, String> {

    SMTPSettings findByName(String name);

}
