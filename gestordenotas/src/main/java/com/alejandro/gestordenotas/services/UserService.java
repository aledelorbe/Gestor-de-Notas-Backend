package com.alejandro.gestordenotas.services;

import java.security.Principal;
import java.util.Optional;

import com.alejandro.gestordenotas.entities.User;

public interface UserService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for the user role
    // -----------------------------

    Optional<User> findById(Long id);
    
    User save(User user);
    
    Optional<User> update(Long id, User user);
    
    Optional<User> deleteById(Long id);

    // Aux Methods 

    boolean isOwner(Long id, Principal principal);

    // -----------------------------
    // Methods for custom queries of the user entity
    // -----------------------------

    Optional<User> getByUsername(String username);

}