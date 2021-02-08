package com.vut.fit.pdb2020.controller;

import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PhotoSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.UserSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PageSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.PhotoSqlRepository;
import com.vut.fit.pdb2020.database.mariaDB.repository.UserSqlRepository;
import com.vut.fit.pdb2020.utils.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

@RestController
public class PhotoController {

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    PhotoSqlRepository photoSqlRepository;

    @Autowired
    ServletContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    FileUtility fileUtility;

    @Transactional
    @PostMapping("/photo/upload/user")
    public String uploadUserPhoto(@RequestParam String ownerEmail, @RequestParam MultipartFile file) throws IOException {

        assert ownerEmail != null && !file.isEmpty();

        UserSql userSql = userSqlRepository.findByEmail(ownerEmail);
        assert userSql != null;

        File dest = fileUtility.saveFile(file, null, userSql.getEmail());

        PhotoSql photoSql = new PhotoSql();

        String filePath = fileUtility.uploadsDir.concat(dest.getName());

        photoSql.setPath(filePath);
        photoSql.setUser(userSql);
        photoSqlRepository.save(photoSql);

        return filePath;

    }

    @Transactional
    @PostMapping("/photo/upload/page")
    public String uploadPagePhoto(@RequestParam Long ownerId, @RequestParam MultipartFile file) throws IOException {

        assert ownerId != null && !file.isEmpty();

        PageSql pageSql = pageSqlRepository.findById(ownerId);
        assert pageSql != null;

        File dest = fileUtility.saveFile(file, pageSql.getId(), null);

        PhotoSql photoSql = new PhotoSql();

        String filePath = fileUtility.uploadsDir.concat(dest.getName());

        photoSql.setPath(filePath);
        photoSql.setPage(pageSql);
        photoSqlRepository.save(photoSql);

        return filePath;

    }

    @Transactional
    @PostMapping("/photo/delete")
    public void deletePhoto(@RequestParam String path) {
        File toDelete = new File(path);
        if  (toDelete.delete()) {
            PhotoSql photo = photoSqlRepository.findByPath(path);
            photo.setDeleted(true);
            photo.setUpdated_at(Instant.now());

            photoSqlRepository.save(photo);
        }
    }

}
