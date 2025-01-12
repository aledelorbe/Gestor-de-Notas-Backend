package com.alejandro.gestordenotas.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.services.UserService;
import com.alejandro.gestordenotas.services.dto.UserDto;

@RestController // To create a api rest.
@RequestMapping("/api/admins") // To create a base path.
public class AdminController {

    // To Inject the service dependency
    @Autowired
    private UserService service;
    
    // To Inject the 'user controller' dependency
    @Autowired
    private SuperAdminController superAdminController;

    // Endpoint's for admin role ----------------------
    
    // To create an endpoint that allows invoking the method
    // 'getAllUsersWithRoleUser'.
    @GetMapping("/users")
    public ResponseEntity<?> getUsersWithRoleUser() {
        return ResponseEntity.ok(service.getAllUsersWithRoleUser());
    }
    
    // To create an endpoint that allows invoking the method
    // 'getUserWithRoleUser'.
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWithRoleUser(@PathVariable Long userId) {

        // Search for a specific user and if it's present then return it.
        Optional<UserDto> optionalUser = service.getUserWithRoleUser(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.orElseThrow());
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the method
    // 'enableUser'.
    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> enableUser(@PathVariable Long userId) {

        List<Long> ids = service.getAllIdWithRoleAdminAndSuperAdmin();

        // If the list of id's contains the 'userId' it means that the admin user
        // wants to delete a user with role admin or super admin.
        if (ids.contains(userId)) {
            // Else returns code response 404
            return ResponseEntity.notFound().build();
        }

        return superAdminController.superAdminEnableUser(userId);
    }

}
