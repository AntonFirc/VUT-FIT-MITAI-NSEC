package com.vut.fit.pdb2020.database.cassandra.repository;

import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.mariaDB.domain.ProfileDictionarySql;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ProfileDictionaryRepository extends CassandraRepository<ProfileDictionaryCql, String> {

    ProfileDictionaryCql findByPath(String path);

    void deleteByPath(String path);

}
