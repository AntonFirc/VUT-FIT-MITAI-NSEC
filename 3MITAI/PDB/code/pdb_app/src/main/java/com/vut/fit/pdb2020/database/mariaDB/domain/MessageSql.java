package com.vut.fit.pdb2020.database.mariaDB.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message")
@Where(clause = "deleted=0")
public class MessageSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserSql author;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatSql chat;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

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

    public UserSql getAuthor() {
        return author;
    }

    public void setAuthor(UserSql author) {
        this.author = author;
    }

    public ChatSql getChat() {
        return chat;
    }

    public void setChat(ChatSql chat) {
        this.chat = chat;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
