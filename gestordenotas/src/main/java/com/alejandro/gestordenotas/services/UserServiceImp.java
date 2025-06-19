package com.alejandro.gestordenotas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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

            return Optional.of(repository.save(userDb));
        }

        return optionalUser;
    }

    // To delete a specific user based on its id
    @Override
    @Transactional
    public Optional<User> deleteById(Long id) {
        // Search for a specific user
        Optional<User> optionalUser = repository.findById(id);

        // If the user is present then delete that user
        optionalUser.ifPresent(userDb -> {
            repository.deleteById(id);
        });

        return optionalUser;
    }

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    // To get a certain user based on its username
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return repository.findByUsername(username);
    }

}