package com.alejandro.gestordenotas.security;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.UserService;

@Component
public class Access {
    
    // To Inject the service dependency
    @Autowired
    private UserService service;
    
    // To know if the user who wants to access the resource is the owner or not
    public boolean isOwner(Long id, Principal principal) {
        boolean result = true;

        // Get the username of the authenticated user
        String authenticatedUsername = principal.getName();

        // Check if the authenticated user is the owner of the resource
        Optional<User> optionalUser1 = service.findById(id);

        if (optionalUser1.isPresent()) {
            User userDb = optionalUser1.get();

            if (!userDb.getUsername().equals(authenticatedUsername)) {
                result = false;
            }
        }

        return result;
    }

}
