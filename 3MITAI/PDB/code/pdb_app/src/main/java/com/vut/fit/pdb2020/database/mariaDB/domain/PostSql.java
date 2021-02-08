package com.vut.fit.pdb2020.database.mariaDB.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "post")
@Where(clause = "deleted=0")
public class PostSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column
    private String content_type;

    @Column
    private String content;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserSql user;

    @OneToOne
    @JoinColumn(name = "page_id")
    private PageSql page;

    @OneToOne
    @JoinColumn(name = "wall_id")
    private WallSql wall;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

    public PostSql() {
        this.deleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserSql getUser() {
        return user;
    }

    public void setUser(UserSql user) {
        this.user = user;
    }

    public PageSql getPage() {
        return page;
    }

    public void setPage(PageSql page) {
        this.page = page;
    }

    public WallSql getWall() {
        return wall;
    }

    public void setWall(WallSql wall) {
        this.wall = wall;
    }

    public Instant getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(Instant created_at) {
        this.createdAt = created_at;
    }

    public Instant getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Instant updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
