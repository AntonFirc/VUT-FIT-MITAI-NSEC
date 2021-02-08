package com.vut.fit.pdb2020.controller.query;

import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.PageDetailDto;
import com.vut.fit.pdb2020.database.dto.converter.PageDtoConverter;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import com.vut.fit.pdb2020.utils.FileUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PageQueryController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    PageDtoConverter pageDtoConverter;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Transactional
    @GetMapping("/page/{profileSlug}")
    public PageDetailDto getPageProfile(@PathVariable("profileSlug") String profileSlug) {

        assert profileSlug != null;

        Long id;
        String profilePath = String.format("/page/%s", profileSlug);

        ProfileDictionaryCql profileDictionaryCql = profileDictionaryRepository.findByPath(profilePath);

        assert profileDictionaryCql != null;

        id = profileDictionaryCql.getPage_id();

        PageDetailDto pageDetailDto = null;

        PageCql pageCql = pageRepository.findById(id);
        if (pageCql != null) {
            pageCql.setLast_active(Instant.now());
            pageRepository.save(pageCql);
            pageDetailDto = pageDtoConverter.cqlToDetail(pageCql);
        }
        else {

            PageSql pageSql = pageSqlRepository.findById(id);
            assert pageSql != null;

            pageDetailDto = pageDtoConverter.sqlToDetail(pageSql);
        }

        return pageDetailDto;

    }

    @GetMapping("/pages/active")
    public List<PageCql> getAllActivePages() {
        return pageRepository.findAll();
    }

    @GetMapping("/pages/all")
    public List<PageSql> getAllPages() {
        return pageSqlRepository.findAll();
    }
}
