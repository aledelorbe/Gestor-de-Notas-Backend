package com.alejandro.gestordenotas.repositories;

import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.entities.Note;

public interface NoteRepository extends CrudRepository<Note, Long> {
    
}
