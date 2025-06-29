package com.alejandro.gestordenotas.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.dto.SuperAdminDto;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.AdminService;
import com.alejandro.gestordenotas.services.SuperAdminService;

@RestController // To create a api rest.
@RequestMapping("/api/super-admins") // To create a base path.
public class SuperAdminController {

    // To Inject the service dependency
    @Autowired
    private SuperAdminService service;

    // To Inject the service dependency
    @Autowired
    private AdminService adminService;

    // Endpoint's for super admin role ----------------------

    // To create an endpoint that allows invoking the 'getAllUsersWithUserAndAdminRole' method.
    @GetMapping("/users-and-admins")
    public ResponseEntity<?> getAllUsersWithUserAndAdminRole() {
        return ResponseEntity.ok(service.getAllUsersWithUserAndAdminRole());
    }

    // To create an endpoint that allows invoking the 'getUserWithUserAndAdminRole' method.
    @GetMapping("/user-and-admin/{userId}")
    public ResponseEntity<?> getUserWithUserAndAdminRole(@PathVariable Long userId) {

        // Search for a specific user and if it's present then return it.
        Optional<SuperAdminDto> optionalUser = service.getUserWithUserAndAdminRole(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.orElseThrow());
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows converting a user into an administrator
    // user
    @PatchMapping("/convert-user-into-admin/{userId}")
    public ResponseEntity<?> addRemoveAdminRoleFromUser(@PathVariable Long userId) {

        // If the user to remove the admin role is a user with the super admin
        // role, then do not allow that operation.
        if (service.isSuperAdmin(userId)) {
            // Else, return a 404 status code.
            return ResponseEntity.notFound().build();
        }

        // Call the 'addRemoveAdminRoleFromUser' method
        Optional<User> optionalUser = service.addRemoveAdminRoleFromUser(userId);

        // If the user is present, it means the operation to add or remove 
        // the admin role from user was successful.
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            SuperAdminDto superAdminDto = new SuperAdminDto(user.getId(), user.getUsername(), user.isEnabled(), user.isAdmin());
            return ResponseEntity.ok(superAdminDto);
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the 'disableEnableUser' method.
    @PatchMapping("/disable-enable-user/{userId}")
    public ResponseEntity<?> superAdminEnableUser(@PathVariable Long userId) {

        // If the user to remove the admin role is a user with the super admin
        // role, then do not allow that operation.
        if (service.isSuperAdmin(userId)) {
            // Else, return a 404 status code.
            return ResponseEntity.notFound().build();
        }

        // Call the 'disableEnableUser' method
        Optional<User> optionalUser = adminService.disableEnableUser(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            SuperAdminDto superAdminDto = new SuperAdminDto(user.getId(), user.getUsername(), user.isEnabled(), user.isAdmin());
            return ResponseEntity.ok(superAdminDto);
        }
        
        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

}
