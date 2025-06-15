package com.alejandro.gestordenotas.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.NoteRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;


@Service
public class NoteServiceImp implements NoteService {

    // To inject the repository dependency.
    @Autowired
    private NoteRepository repository;

    // To inject the repository dependency.
    @Autowired
    private UserRepository userRepository;

    // -----------------------------
    // Methods for note entity
    // -----------------------------

    // To get a specific note based on its id
    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findById(Long id) {
        return repository.findById(id);
    }

    // To save a new note of a certain user in the db
    @Override
    @Transactional
    public Optional<User> saveNoteByUser(Long userId, Note newNote) {

        // Search for a specific user
        Optional<User> optionalUser = userRepository.findById(userId);

        // if it exists then save the note and return the new data (new user)
        if ( optionalUser.isPresent() ) {
            User userDb = optionalUser.get();

            userDb.getNotes().add(newNote);
    
            return Optional.of(userRepository.save(userDb));
        }

        // Else, return an empty optional
        return optionalUser;
    }

    // To update the information about the note
    @Override
    @Transactional
    public Optional<User> editNoteByUser(Long userId, Long noteId, Note editNote) {

        // Search for a specific user and specific note
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Note> optionalNote = repository.findById(noteId);

        // If the user and note are present then ...
        if ( optionalUser.isPresent() && optionalNote.isPresent() ) {
        
            // Check if the note belongs to this user.
            User userDb = optionalUser.get();
            Optional<Note> optionalBelongingNote = userDb.getNotes().stream().filter(many -> many.getId().equals(noteId)).findFirst();

            // If this note is present it means the user is owner of note
            if (optionalBelongingNote.isPresent()) {
                // Update all of object attributes 
                Note noteDb = optionalBelongingNote.get();

                noteDb.setContent(editNote.getContent());

                // and save the information in the db
                return Optional.of(userRepository.save(userDb));
            }

            // Else, return an empty optional
            return Optional.empty();
        }
        
        // Else, return an empty optional
        return Optional.empty();
    }

    // To delete a certain note in the db
    @Override
    @Transactional
    public Optional<User> deleteNoteByUser(Long userId, Long noteId) {

        // Search for a specific user and specific note
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Note> optionalNote = repository.findById(noteId);

        // If the user and note are present then ...
        if ( optionalUser.isPresent() && optionalNote.isPresent() ) {
        
            // Search for the note that will be deleted
            User userDb = optionalUser.get();
            Optional<Note> optionalBelongingNote = userDb.getNotes().stream().filter(many -> many.getId().equals(noteId)).findFirst();

            // If this note is present it means the user is owner of note
            if (optionalBelongingNote.isPresent()) {
                // Delete the note
                userDb.getNotes().remove(optionalBelongingNote.get());

                // and save the information in the db
                return Optional.of(userRepository.save(userDb));
            }

            // Else, return an empty optional
            return Optional.empty();
        }
        
        // Else, return an empty optional
        return Optional.empty();
    }

}