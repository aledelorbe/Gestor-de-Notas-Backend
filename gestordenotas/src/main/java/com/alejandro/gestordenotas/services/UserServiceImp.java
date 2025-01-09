package com.alejandro.gestordenotas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.Role;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.RoleRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;

@Service
public class UserServiceImp implements UserService {

    // To inject the repository dependency.
    @Autowired
    private UserRepository repository;

    // To inject the repository dependency.
    @Autowired
    private RoleRepository roleRepository;

    // To be able to encrypt passwords
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // // To save a new user in the db
    // // This method is a 'join point'
    // @Override
    // @Transactional
    // public User save(User user) {
    // return repository.save(user);
    // }

    // To save a new user in the db
    // This method is a 'join point'
    @Override
    @Transactional
    public User save(User user) {
        // Search for the rol called 'role_user' in the table 'role'
        // (All of the users register must have at least this role) *******
        Set<Role> roles = new HashSet<>();
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        // If this role is present then add the role to list of roles
        optionalRoleUser.ifPresent(roles::add);

        // But only if the new user must be admin then add this role to list of roles
        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        // Add all of the roles to new user
        user.setRoles(roles);
        // Encrypt the password of the user and save the user in the db
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

            userDb.setUsername(user.getUsername());
            userDb.setPassword(passwordEncoder.encode(user.getPassword()));

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
    public Optional<User> editNoteByUserId(User userDb, Long noteId, Note editNote) {

        // Search for the note that will be updated
        Optional<Note> optionalNote = userDb.getNotes().stream().filter(many -> many.getId().equals(noteId))
                .findFirst();

        // If this note is present it means the user is owner of note
        if (optionalNote.isPresent()) {
            // Update all of object attributes (in this case update only 'content'
            // attribute)
            Note noteDb = optionalNote.get();

            noteDb.setContent(editNote.getContent());

            // and save the information in the db
            return Optional.of(repository.save(userDb));
        }

        return Optional.empty();
    }

    // To delete a certain note in the db
    @Override
    @Transactional
    public Optional<User> deleteNoteByUserId(User userDb, Long noteId) {

        // Search for the note that will be updated
        Optional<Note> optionalNote = userDb.getNotes().stream().filter(many -> many.getId().equals(noteId)).findFirst();
        
        // If this note is present it means the user is owner of note
        if (optionalNote.isPresent()) {
            // Delete the note
            userDb.getNotes().remove(optionalNote.get());

            // and save the information in the db
            return Optional.of(repository.save(userDb)); 
        }

        return Optional.empty();
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