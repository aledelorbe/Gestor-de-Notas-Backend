package com.alejandro.gestordenotas.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.alejandro.gestordenotas.entities.Note;

import com.alejandro.gestordenotas.repositories.NoteRepository;


@Service
public class NoteServiceImp implements NoteService {

    // To inject the repository dependency.
    @Autowired
    private NoteRepository repository;

    // To get a specific pet based on its id
    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findById(Long id) {
        return repository.findById(id);
    }
}