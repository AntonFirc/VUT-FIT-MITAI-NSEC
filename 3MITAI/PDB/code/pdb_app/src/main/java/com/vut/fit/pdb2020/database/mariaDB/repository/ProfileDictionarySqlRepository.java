package com.vut.fit.pdb2020.database.mariaDB.repository;

import com.vut.fit.pdb2020.database.mariaDB.domain.ProfileDictionarySql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileDictionarySqlRepository extends JpaRepository<ProfileDictionarySql, String> {

    ProfileDictionarySql findById(Long profile_path);

    ProfileDictionarySql findByPath(String path);

}
