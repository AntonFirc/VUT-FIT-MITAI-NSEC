package com.vut.fit.pdb2020.database.mariaDB.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "page")
@Where(clause="deleted=0")
public class PageSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column
    private String name;

    @OneToOne
    @JoinColumn(name = "admin_id")
    private UserSql admin;

    @OneToOne
    @JoinColumn(name = "profile_photo_id")
    private PhotoSql profilePhoto;

    @OneToOne
    @JoinColumn(name = "wall_id")
    private WallSql wall;

    @Column
    private Instant created_at;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

    @Transient
    private String profilePath;

    public PageSql() {
        this.deleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserSql getAdmin() {
        return admin;
    }

    public void setAdmin(UserSql admin) {
        this.admin = admin;
    }

    public PhotoSql getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(PhotoSql profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public WallSql getWall() {
        return wall;
    }

    public void setWall(WallSql wall) {
        this.wall = wall;
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

    public String getProfilePath() {
        if (profilePath == null) {
            profilePath = String.format("/page/%s.%d", name.toLowerCase().replaceAll("\\s+",""), id);
        }
        return profilePath;
    }

    public String getProfilePhotoPath() {
        if (profilePhoto != null) {
            return profilePhoto.getPath();
        }
        return null;
    }
}
