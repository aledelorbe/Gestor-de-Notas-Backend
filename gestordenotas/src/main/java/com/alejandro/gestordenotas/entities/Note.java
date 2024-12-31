package com.alejandro.gestordenotas.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

// To specific the name of the table in mysql
// In mysql the name of this table is 'note' but in this project 
// the name of this class is 'Note'
@Entity
@Table(name = "note")
public class Note {

    // Mapping of class attributes with table fields in mysql

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_note")
    private Long id;

    @NotBlank // To obligate to this attribute not to empty or blank values.
    private String content;

    // These attributes can have empty values because they are set in cycle life events of entity objects
    private LocalDate createdAt;

    private LocalDate updatedAt;

    public Note() {
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    // To set the date when the record is saved in the db 
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    // To set the date when the record is updated in the db 
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }

}
