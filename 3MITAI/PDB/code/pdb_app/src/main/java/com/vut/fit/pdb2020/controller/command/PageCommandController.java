package com.vut.fit.pdb2020.controller.command;

import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.PageDto;
import com.vut.fit.pdb2020.database.dto.converter.PageDtoConverter;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import com.vut.fit.pdb2020.database.mariaDB.service.PageService;
import com.vut.fit.pdb2020.utils.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PageCommandController {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    UserPageSqlRepository userPageSqlRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PhotoSqlRepository photoSqlRepository;

    @Autowired
    PageDtoConverter pageDtoConverter;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    @Autowired
    FileUtility fileUtility;

    @Autowired
    PageService pageService;

    @Transactional
    @PostMapping("/page/create")
    public Long createPage(@RequestParam String email, @RequestParam String name) {

        assert email != null;

        PageDto pageDto = new PageDto();
        pageDto.setAdminEmail(email);
        pageDto.setName(name);

        return pageService.createPage(pageDto);

    }

    @Transactional
    @PostMapping("/page/delete")
    public void deletePage(@RequestParam Long id) {

        assert id != null;

        PageDto pageDto = new PageDto();
        pageDto.setId(id);
        pageDto.setDelete(true);

        pageService.deletePage(pageDto);

    }

    @Transactional
    @PostMapping("/page/profilePic")
    public void addPageProfilePic(@RequestParam Long id, @RequestParam MultipartFile file) throws IOException {

        assert id != null;

        PageSql page = pageSqlRepository.findById(id);
        assert page != null;

        File dest = fileUtility.saveFile(file, page.getId(), null);

        PhotoSql photoSql = new PhotoSql();

        String filePath = fileUtility.uploadsDir.concat(dest.getName());

        photoSql.setPath(filePath);
        photoSql.setPage(page);
        photoSql = photoSqlRepository.save(photoSql);

        page.setProfilePhoto(photoSql);
        page.setUpdated_at(Instant.now());
        pageSqlRepository.save(page);

        PageDto pageDto = new PageDto();
        pageDto.setId(page.getId());
        pageDto.setProfilePicPath(page.getProfilePhotoPath());

        pageService.addPhoto(pageDto);

    }

    @Transactional
    @PostMapping("/page/profilePic/delete")
    public void pageRemoveProfilePic(@RequestParam Long id) {

        assert id != null;

        PageSql page = pageSqlRepository.findById(id);
        assert page != null;

        PhotoSql photoSql = page.getProfilePhoto();
        assert photoSql != null;
        page.setProfilePhoto(null);
        pageSqlRepository.save(page);

        photoSql.setDeleted(true);
        photoSql.setUpdated_at(Instant.now());
        photoSqlRepository.save(photoSql);

        PageDto pageDto = new PageDto();
        pageDto.setId(page.getId());

        pageService.deletePhoto(pageDto);

    }

}
