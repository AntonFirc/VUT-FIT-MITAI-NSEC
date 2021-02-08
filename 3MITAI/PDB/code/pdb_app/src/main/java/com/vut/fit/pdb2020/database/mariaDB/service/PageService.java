package com.vut.fit.pdb2020.database.mariaDB.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.dto.PageDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.*;
import com.vut.fit.pdb2020.database.mariaDB.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

public interface PageService {

    public Long createPage(PageDto pageDto);

    public void deletePage(PageDto pageDto);

    public void addPhoto(PageDto pageDto);

    public void deletePhoto(PageDto pageDto);

}

@Service
class PageServiceImpl implements PageService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Autowired
    UserSqlRepository userSqlRepository;

    @Autowired
    WallSqlRepository wallSqlRepository;

    @Autowired
    PageSqlRepository pageSqlRepository;

    @Autowired
    UserPageSqlRepository userPageSqlRepository;

    @Autowired
    PhotoSqlRepository photoSqlRepository;

    @Autowired
    ProfileDictionarySqlRepository profileDictionarySqlRepository;

    @Override
    public Long createPage(PageDto pageDto) {

        UserSql userSql = userSqlRepository.findByEmail(pageDto.getAdminEmail());
        assert userSql != null;

        WallSql pageWall = new WallSql();
        pageWall = wallSqlRepository.save(pageWall);

        PageSql page = new PageSql();
        page.setAdmin(userSql);
        // If name not provided generate it
        page.setName((pageDto.getName() == null) ? userSql.getName().concat("'s page") : pageDto.getName());
        page.setWall(pageWall);

        page = pageSqlRepository.save(page);

        UserPageSql userPageSql = new UserPageSql();
        userPageSql.setUser(userSql);
        userPageSql.setIs_admin(true);
        userPageSql.setPage(page);

        userPageSqlRepository.save(userPageSql);

        ProfileDictionarySql profileDict = new ProfileDictionarySql();
        profileDict.setPage(page);
        profileDict.setPath(page.getProfilePath());
        profileDictionarySqlRepository.save(profileDict);

        pageDto.setId(page.getId());
        pageDto.setProfilePath(page.getProfilePath());

        this.raiseEvent(pageDto);

        return page.getId();

    }

    @Override
    public void deletePage(PageDto pageDto) {

        PageSql pageSql = pageSqlRepository.findById(pageDto.getId());
        assert pageSql != null;

        pageSql.setDeleted(true);
        pageSql.setUpdated_at(Instant.now());
        pageSqlRepository.save(pageSql);

        PhotoSql photo = pageSql.getProfilePhoto();
        if (photo != null) {
            photo.setDeleted(true);
            photo.setUpdated_at(Instant.now());
            photoSqlRepository.save(photo);
        }

        WallSql wall = pageSql.getWall();
        if (wall != null) {
            wall.setDeleted(true);
            wall.setUpdated_at(Instant.now());
            wallSqlRepository.save(wall);
        }

        ProfileDictionarySql profileDictionarySql = profileDictionarySqlRepository.findByPath(pageSql.getProfilePath());
        if (profileDictionarySql != null) {
            profileDictionarySql.setDeleted(true);
            profileDictionarySql.setUpdated_at(Instant.now());
            profileDictionarySqlRepository.save(profileDictionarySql);
        }

        UserPageSql userPageSql = userPageSqlRepository.findByPage(pageSql);
        assert userPageSql != null;

        userPageSql.setDeleted(true);
        userPageSql.setUpdated_at(Instant.now());
        userPageSqlRepository.save(userPageSql);

        pageDto.setAdminEmail(pageSql.getAdmin().getEmail());
        pageDto.setProfilePath(pageSql.getProfilePath());

        this.raiseEvent(pageDto);

    }

    @Override
    public void addPhoto(PageDto pageDto) {
        pageDto.setPhotoUpdate(true);
        this.raiseEvent(pageDto);
    }

    @Override
    public void deletePhoto(PageDto pageDto) {
        pageDto.setPhotoUpdate(true);
        pageDto.setDelete(true);
        this.raiseEvent(pageDto);
    }

    private void raiseEvent(PageDto dto) {
        try {
            String value = OBJECT_MAPPER.writeValueAsString(dto);
            this.kafkaTemplate.send("page-service-event", dto.getId(), value);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}