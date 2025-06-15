package com.alejandro.gestordenotas.services;

import java.util.Optional;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;


public interface NoteService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for note entity
    // -----------------------------

    Optional<Note> findById(Long id);
    
    Optional<User> saveNoteByUser(Long clientId, Note newNote);
    
    Optional<User> editNoteByUser(Long clientId, Long noteId, Note editNote);
    
    Optional<User> deleteNoteByUser(Long clientId, Long noteId);

}