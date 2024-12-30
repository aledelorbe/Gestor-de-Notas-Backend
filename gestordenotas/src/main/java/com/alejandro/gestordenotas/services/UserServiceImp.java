package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // To change the enabled attribute a specific user based on its id from active to inactive and vice verse.
    // @Override
    // @Transactional
    // public Optional<User> updateEnabledById(Long id) {
    //     // Search a specific user
    //     Optional<User> optionalUser = repository.findById(id);

    //     // If the user is present then...
    //     if (optionalUser.isPresent()) {
    //         // change the enabled attribute
    //         User userDb = optionalUser.get();

    //         if( userDb.getEnabled() == 1 ) {
    //             userDb.setEnabled(0);
    //         } else {
    //             userDb.setEnabled(1);
    //         }

    //         return Optional.ofNullable(repository.save(userDb));
    //     }

    //     return optionalUser;
    // }

}