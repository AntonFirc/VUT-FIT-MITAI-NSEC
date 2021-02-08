package com.vut.fit.pdb2020.database.cassandra.domain;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Comment;
import com.vut.fit.pdb2020.database.cassandra.dataTypes.Like;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Table("page_post")
public class PagePostCql {

    @Column("page_id")
    private Long pageId;

    @Column("content_type")
    private String contentType;

    /* WARNING, this gives absolutely no sense... but table needs to have primary key annotated, but this
     * overrides the column name annotation thus breaks repository operations.. */
    @PrimaryKey
    private String content;

    private String page_name;

    private List<Comment> comments;

    private List<Like> likes;

    @Column("created_at")
    private Instant createdAt;

    public Long getPage_id() {
        return pageId;
    }

    public void setPage_id(Long page_id) {
        this.pageId = page_id;
    }

    public String getContent_type() {
        return contentType;
    }

    public void setContent_type(String content_type) {
        this.contentType = content_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public TreeSet<Comment> getTreeComments() { return new TreeSet<>(comments); }

    public void setTreeComments(TreeSet<Comment> comments) {this.comments = new ArrayList<>(comments); }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public Instant getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(Instant created_at) {
        this.createdAt = created_at;
    }

    public TreeSet<Like> getTreeLikes() { return new TreeSet<>(likes); }

    public void setTreeLikes(TreeSet<Like> likes) {this.likes = new ArrayList<>(likes); }
}
