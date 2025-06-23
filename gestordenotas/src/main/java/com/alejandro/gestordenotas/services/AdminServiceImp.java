package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.dto.AdminDto;
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
    public List<AdminDto> getAllUsersWithUserRole() {
        return repository.getAllUsersWithUserRole();
    }

    // To get a specific user with the user role based on their user ID
    @Override
    @Transactional(readOnly = true)
    public Optional<AdminDto> getUserWithUserRole(Long id) {
        return repository.getUserWithUserRole(id);
    }

    // To enable or disable a certain user
    @Override
    @Transactional
    public Optional<User> disableEnableUser(Long userId) {

        // Search for a specific user
        Optional<User> optionalUser = repository.findById(userId);

        // If it's present then...
        if (optionalUser.isPresent()) {

            User userDb = optionalUser.get();
            
            // If the user is enabled then disable it
            if (userDb.isEnabled()) {
                userDb.setEnabled(false);
            } else {
                userDb.setEnabled(true);
            }

            return Optional.of(repository.save(userDb));
        }

        return optionalUser;
    }

    // Methods aux ----------------------------------
    
    // To get all of the id's of users with role 'admin' and 'super admin'
    @Override
    @Transactional(readOnly = true)
    public List<Long> getAllIdsWithAdminAndSuperAdminRole() {
        return repository.getAllIdsWithAdminAndSuperAdminRole();
    }

}
