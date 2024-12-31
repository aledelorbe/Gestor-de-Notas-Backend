package com.alejandro.gestordenotas.services;

import java.util.Optional;

import com.alejandro.gestordenotas.entities.Note;


public interface NoteService {

    // Declaration of methods to use in 'serviceImp' file

    public Optional<Note> findById(Long id);

}