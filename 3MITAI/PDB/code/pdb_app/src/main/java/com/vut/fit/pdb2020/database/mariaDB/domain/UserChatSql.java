package com.vut.fit.pdb2020.database.mariaDB.domain;

import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name="user_chat")
@EntityListeners(AuditingEntityListener.class)
@Where(clause="deleted=0")
public class UserChatSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserSql user;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatSql chat;

    @Column
    private Instant created_at;

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

    public UserSql getUser() {
        return user;
    }

    public void setUser(UserSql user) {
        this.user = user;
    }

    public ChatSql getChat() {
        return chat;
    }

    public void setChat(ChatSql chat) {
        this.chat = chat;
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
