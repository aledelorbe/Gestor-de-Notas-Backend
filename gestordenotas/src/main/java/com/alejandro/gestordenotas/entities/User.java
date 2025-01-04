package com.alejandro.gestordenotas.entities;

import java.util.ArrayList;
import java.util.List;

// import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;

// To specific the name of the table in mysql
// In mysql the name of this table is 'user' but in this project 
// the name of this class is 'User'
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(name = "UK_user", columnNames = { "username" }))
public class User {

    // Mapping of class attributes with table fields in mysql

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @NotBlank // To obligate to this attribute not to empty or blank values.
    private String username;

    @NotBlank // To obligate to this attribute not to empty or blank values.
    private String password;

    // To set a relationship one to many
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_user")
    private List<Note> notes;

    public User() {
        this.notes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // @JsonIgnore // To not send the information about 'notes'
    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}