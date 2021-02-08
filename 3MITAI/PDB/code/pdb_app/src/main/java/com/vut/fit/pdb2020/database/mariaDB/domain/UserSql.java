package com.vut.fit.pdb2020.database.mariaDB.domain;

import com.vut.fit.pdb2020.database.dto.UserCreateDto;
import com.vut.fit.pdb2020.database.mariaDB.repository.StateSqlRepository;
import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name="user")
@EntityListeners(AuditingEntityListener.class)
@Where(clause="deleted=0")
public class UserSql implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password_hash;

    @OneToOne
    @JoinColumn(name = "profile_photo_id")
    private PhotoSql profilePhoto;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @OneToOne
    @JoinColumn(name = "state_id")
    private StateSql state;

    @OneToOne
    @JoinColumn(name = "wall_id")
    private WallSql wall;

    @ManyToMany
    private List<ChatSql> chats;

    @Column
    private Instant created_at;

    @Column
    private Instant updated_at;

    @Column
    private Boolean deleted;

    @Transient
    private String profilePath;

    public UserSql() {
        this.deleted = false;
    }

    public UserSql(String name, String surname, String email, String password_hash, String gender, String address, String city, StateSql state) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password_hash = password_hash;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.state = state;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public PhotoSql getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(PhotoSql profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setState(StateSql state) {
        this.state = state;
    }

    public StateSql getState() {
        return state;
    }

    public void setStateId(StateSql state) {
        this.state = state;
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
            profilePath = String.format("/user/%s%s.%d",
                    name.toLowerCase().replaceAll("\\s+",""),
                    surname.toLowerCase().replaceAll("\\s+",""),
                    id);
        }
        return profilePath;
    }

    public String getProfilePhotoPath() {
        if (profilePhoto != null) {
            return profilePhoto.getPath();
        }
        return null;
    }

    public String getFullName() {
        return String.format("%s %s", name, surname);
    }

}
