package com.vut.fit.pdb2020.database.cassandra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.ProfileDictionaryCql;
import com.vut.fit.pdb2020.database.cassandra.domain.UserCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PageRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.ProfileDictionaryRepository;
import com.vut.fit.pdb2020.database.cassandra.repository.UserRepository;
import com.vut.fit.pdb2020.database.dto.CommentLikeDto;
import com.vut.fit.pdb2020.database.dto.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface PageServiceEventHandler {

    public void createPage(PageDto pageDto);

    public void deletePage(PageDto pageDto);

    public void addPhoto(PageDto pageDto);

    public void deletePhoto(PageDto pageDto);

}

@Service
class PageServiceEventHandlerImpl implements PageServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    UserRepository userRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ProfileDictionaryRepository profileDictionaryRepository;

    @KafkaListener(topics = "page-service-event")
    public void consume(String likeStr) {
        try{
            PageDto pageDto = OBJECT_MAPPER.readValue(likeStr, PageDto.class);
            if (pageDto.isPhotoUpdate()) {
                if (pageDto.isDelete()) {
                    this.deletePhoto(pageDto);
                }
                else {
                    this.addPhoto(pageDto);
                }
                return;
            }
            if (!pageDto.isDelete()) {
                this.createPage(pageDto);
            } else {
                this.deletePage(pageDto);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void createPage(PageDto pageDto) {

        UserCql userCql = userRepository.findByEmail(pageDto.getAdminEmail());

        PageCql pageCql = new PageCql();
        pageCql.setId(pageDto.getId());
        pageCql.setAdmin_email(userCql.getEmail());
        pageCql.setName(pageDto.getName());
        pageCql.setProfile_path(pageDto.getProfilePath());

        pageCql = pageRepository.save(pageCql);

        ProfileDictionaryCql profileDictionaryCql = new ProfileDictionaryCql();
        profileDictionaryCql.setProfile_path(pageCql.getProfile_path());
        profileDictionaryCql.setPage_id(pageCql.getId());
        profileDictionaryRepository.save(profileDictionaryCql);

        List<Long> newPages = userCql.getOwned_pages();
        if (newPages == null)
            newPages = new ArrayList<>();
        newPages.add(pageCql.getId());

        userCql.setOwned_pages(newPages);
        userRepository.save(userCql);

    }

    @Override
    public void deletePage(PageDto pageDto) {

        UserCql userCql = userRepository.findByEmail(pageDto.getAdminEmail());
        if (userCql != null) {
            List<Long> newPages = userCql.getOwned_pages().stream().filter((pageId) -> !pageId.equals(pageDto.getId())).collect(Collectors.toList());
            userCql.setOwned_pages(newPages);

            profileDictionaryRepository.deleteByPath(pageDto.getProfilePath());
        }
        pageRepository.deleteById(pageDto.getId());

    }

    @Override
    public void addPhoto(PageDto pageDto) {
        PageCql pageCql = pageRepository.findById(pageDto.getId());

        if (pageCql != null) {
            pageCql.setProfile_photo_path(pageDto.getProfilePicPath());
            pageCql.setLast_active(Instant.now());
            pageRepository.save(pageCql);
        }
    }

    @Override
    public void deletePhoto(PageDto pageDto) {
        PageCql pageCql = pageRepository.findById(pageDto.getId());

        if (pageCql != null) {
            pageCql.setProfile_photo_path(null);
            pageRepository.save(pageCql);
        }

    }
}
