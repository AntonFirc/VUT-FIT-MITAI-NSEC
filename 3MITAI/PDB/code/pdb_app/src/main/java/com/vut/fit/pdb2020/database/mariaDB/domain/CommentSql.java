package com.vut.fit.pdb2020.database.mariaDB.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "comment")
@Where(clause = "deleted=0")
public class CommentSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String content;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserSql user;

    @OneToOne
    @JoinColumn(name = "page_id")
    private PageSql page;

    @OneToOne
    @JoinColumn(name = "post_id")
    private PostSql post;

    @Column
    private Instant created_at;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

    public CommentSql() {
        this.deleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PostSql getPost() {
        return post;
    }

    public void setPost(PostSql post) {
        this.post = post;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
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
