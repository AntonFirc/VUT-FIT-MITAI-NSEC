package com.vut.fit.pdb2020.controller;

import com.vut.fit.pdb2020.database.mariaDB.domain.StateSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.StateSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@RestController
public class StateController {

    @Autowired
    StateSqlRepository stateSqlRepository;

    @Transactional
    @PostMapping("/state/create")
    public Long createState(@RequestParam String name) {

        assert name != null;

        StateSql state = stateSqlRepository.findByName(name);
        assert state == null;
        state = new StateSql();

        state.setName(name);
        stateSqlRepository.save(state);

        return state.getId();

    }

    @Transactional
    @PostMapping("/state/delete")
    public void deleteState(@RequestParam Long stateId) {

        assert stateId != null;

        StateSql stateSql = stateSqlRepository.findById(stateId);
        assert stateSql != null;

        stateSql.setDeleted(true);
        stateSql.setUpdated_at(Instant.now());

        stateSqlRepository.save(stateSql);

    }

    @GetMapping("/states")
    public List<StateSql> getStates() {
        return stateSqlRepository.findAll();
    }

}
