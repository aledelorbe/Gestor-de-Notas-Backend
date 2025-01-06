package com.alejandro.gestordenotas.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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

    // This attribute is not mapped to any fields in the table.
    @Transient
    private boolean admin;

    // This attribute can be empty because it is set in cycle life events of entity objects
    private boolean enabled;

    // To set a relationship one to many
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_user")
    private List<Note> notes;

    // To set a relationship many to many
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Role> roles; 

    public User() {
        this.notes = new ArrayList<>();
        this.roles = new HashSet<>();
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // To set the date when the record is saved in the db 
    @PrePersist
    public void prePersist() {
        this.enabled = true;
    }

}