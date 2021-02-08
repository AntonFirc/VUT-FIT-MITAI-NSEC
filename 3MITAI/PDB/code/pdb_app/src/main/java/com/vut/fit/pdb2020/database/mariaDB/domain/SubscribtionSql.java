package com.vut.fit.pdb2020.database.mariaDB.domain;


import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "subscribed_to")
@Where(clause="deleted=0")
public class SubscribtionSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "subscriber_id")
    private UserSql subscriber;

    @OneToOne
    @JoinColumn(name = "subscribed_to_user")
    private UserSql subscribedToUser;

    @OneToOne
    @JoinColumn(name = "subscribed_to_page")
    private PageSql subscribedToPage;

    @Column
    private Instant created_at;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

    public SubscribtionSql() {
        this.deleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSql getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(UserSql subscriber) {
        this.subscriber = subscriber;
    }

    public UserSql getSubscribedToUser() {
        return subscribedToUser;
    }

    public void setSubscribedToUser(UserSql subscribedToUser) {
        this.subscribedToUser = subscribedToUser;
    }

    public PageSql getSubscribedToPage() {
        return subscribedToPage;
    }

    public void setSubscribedToPage(PageSql subscribedToPage) {
        this.subscribedToPage = subscribedToPage;
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
