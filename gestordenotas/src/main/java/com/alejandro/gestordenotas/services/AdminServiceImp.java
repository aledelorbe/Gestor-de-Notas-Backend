package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.UserRepository;

@Service
public class AdminServiceImp implements AdminService {

    // To inject the repository dependency.
    @Autowired
    private UserRepository repository;

    // -----------------------------
    // Methods for the admin role
    // -----------------------------
    
    // To get all of the users with the role 'user'
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersWithRoleUser() {
        return repository.getAllUsersWithRoleUser();
    }

    // To get all of the users with the role 'user'
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserWithRoleUser(Long id) {
        return repository.getUserWithRoleUser(id);
    }

    // To enable or disable a specific 'user'
    @Override
    @Transactional
    public User enabledUser(User userDb) {

        // If the user is enabled then disable it
        if (userDb.isEnabled()) {
            userDb.setEnabled(false);
        } else {
            userDb.setEnabled(true);
        }

        return repository.save(userDb);
    }

}
