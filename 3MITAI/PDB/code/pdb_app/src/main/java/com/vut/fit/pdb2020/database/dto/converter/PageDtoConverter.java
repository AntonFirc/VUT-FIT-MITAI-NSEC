package com.vut.fit.pdb2020.database.dto.converter;

import com.vut.fit.pdb2020.database.cassandra.domain.PageCql;
import com.vut.fit.pdb2020.database.cassandra.domain.PagePostCql;
import com.vut.fit.pdb2020.database.cassandra.repository.PagePostRepository;
import com.vut.fit.pdb2020.database.dto.PageDetailDto;
import com.vut.fit.pdb2020.database.dto.PostDetailDto;
import com.vut.fit.pdb2020.database.mariaDB.domain.PageSql;
import com.vut.fit.pdb2020.database.mariaDB.domain.PostSql;
import com.vut.fit.pdb2020.database.mariaDB.repository.PostSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PageDtoConverter {

    @Autowired
    PagePostRepository pagePostRepository;

    @Autowired
    PostDetialDtoConverter postDetialDtoConverter;

    @Autowired
    PostSqlRepository postSqlRepository;

    public PageDetailDto cqlToDetail(PageCql page) {

        PageDetailDto pageDetailDto = null;

        if (page != null) {
            List<PagePostCql> pagePosts = pagePostRepository.findByPageIdAndContentTypeOrderByCreatedAt(page.getId(), "text");
            List<PagePostCql> pageImgPosts = pagePostRepository.findByPageIdAndContentTypeOrderByCreatedAt(page.getId(), "image");

            pagePosts.addAll(pageImgPosts);
            pagePosts.sort(Comparator.comparing(PagePostCql::getCreated_at).reversed());

            List<PostDetailDto> postDtos = pagePosts.stream().map( post -> postDetialDtoConverter.pagePostCqlToDto(post)).collect(Collectors.toList());

            pageDetailDto = new PageDetailDto(
                    page.getName(),
                    page.getAdmin_email(),
                    page.getProfile_photo_path(),
                    postDtos
            );
        }

        return  pageDetailDto;

    }

    public PageDetailDto sqlToDetail(PageSql page) {

        PageDetailDto pageDetailDto = null;

        if (page != null) {
            List<PostSql> pagePosts = postSqlRepository.findAllByPage(page);

            pagePosts.sort(Comparator.comparing(PostSql::getCreated_at).reversed());

            List<PostDetailDto> postDtos = pagePosts.stream().map( post -> postDetialDtoConverter.postSqlToDto(post)).collect(Collectors.toList());

            pageDetailDto = new PageDetailDto(
                    page.getName(),
                    page.getAdmin().getEmail(),
                    page.getProfilePhotoPath(),
                    postDtos
            );
        }

        return  pageDetailDto;

    }

}
