package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;

import com.alejandro.gestordenotas.repositories.UserRepository;

@Service
public class UserServiceImp implements UserService {

    // To inject the repository dependency.
    @Autowired
    private UserRepository repository;

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // To list all of users (records) in the table 'users'
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll(); // cast because the method findAll returns an iterable.
    }

    // To get a specific user based on its id
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    // To save a new user in the db
    // This method is a 'join point'
    @Override
    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    // To update a specific user based on its id
    @Override
    @Transactional
    public Optional<User> update(Long id, User user) {
        // Find a specific user
        Optional<User> optionalUser = repository.findById(id);

        // If the user is present then...
        if (optionalUser.isPresent()) {
            // update that record and return an optional value
            User userDb = optionalUser.get();

            userDb.setName(user.getName());
            userDb.setPassword(user.getPassword());

            return Optional.ofNullable(repository.save(userDb));
        }

        return optionalUser;
    }

    // To change the enabled attribute a specific user based on its id from active
    // to inactive and vice verse.
    // @Override
    // @Transactional
    // public Optional<User> updateEnabledById(Long id) {
    // // Search a specific user
    // Optional<User> optionalUser = repository.findById(id);

    // // If the user is present then...
    // if (optionalUser.isPresent()) {
    // // change the enabled attribute
    // User userDb = optionalUser.get();

    // if( userDb.getEnabled() == 1 ) {
    // userDb.setEnabled(0);
    // } else {
    // userDb.setEnabled(1);
    // }

    // return Optional.ofNullable(repository.save(userDb));
    // }

    // return optionalUser;
    // }

    // -----------------------------
    // Methods for note entity
    // -----------------------------

    // To save a new note of a certain user in the db
    @Override
    @Transactional
    public User saveNoteByUserId(User userDb, Note newNote) {

        userDb.getNotes().add(newNote);

        return repository.save(userDb);
    }

    // To update the information about the note
    @Override
    @Transactional
    public User editNoteByUserId(User userDb, Note noteDb, Note editNote) {

        // update all of object attributes (in this case update only 'content'
        // attribute)
        noteDb.setContent(editNote.getContent());

        return repository.save(userDb);
    }

    // To delete a certain note in the db
    @Override
    @Transactional
    public User deleteNoteByUserId(User userDb, Note noteDb) {

        userDb.getNotes().remove(noteDb);

        return repository.save(userDb);
    }

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    // To get all the pets of certain user
    @Override
    @Transactional(readOnly = true)
    public List<Note> getNotesByUserId(Long id_user) {
        return repository.getNotesByUserId(id_user);
    }

}