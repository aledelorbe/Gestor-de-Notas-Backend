package com.alejandro.gestordenotas.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.dto.AdminDto;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.AdminService;
import com.alejandro.gestordenotas.services.UserService;
import com.alejandro.gestordenotas.utils.UtilValidation;

import jakarta.validation.Valid;


@RestController // To create a api rest.
@RequestMapping("/api/admins") // To create a base path.
public class AdminController {

    // To Inject the service dependency
    @Autowired
    private UserService userService;

    // To Inject the service dependency
    @Autowired
    private AdminService service;

    @Autowired
    private UtilValidation utilValidation;
    
    // Endpoint's for the admin role ----------------------
    
    // To create an endpoint that allows invoking the 'getAllUsersWithUserRole' method.
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersWithUserRole() {
        return ResponseEntity.ok(service.getAllUsersWithUserRole());
    }
    
    // To create an endpoint that allows invoking the 'getUserWithUserRole' method.
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWithUserRole(@PathVariable Long userId) {

        // Search for a specific user and if it's present then return it.
        Optional<AdminDto> optionalUser = service.getUserWithUserRole(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.orElseThrow());
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the 'saveUser' method (the user
    // will become an admin)
    // The annotation called 'RequestBody' allows receiving data of a user
    @PostMapping()
    public ResponseEntity<?> saveNewUserAdmin(@Valid @RequestBody User user, BindingResult result) {
        // To handle the obligations of object attributes
        if (result.hasFieldErrors()) {
            return utilValidation.validation(result);
        }

        user.setAdmin(true);
        // When a new user is created to respond return the same user
        User newUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // To create an endpoint that allows invoking the 'disableEnableUser' method.
    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> disableEnableUser(@PathVariable Long userId) {

        List<Long> ids = service.getAllIdsWithAdminAndSuperAdminRole();

        // If the list of IDs contains the 'userId', it means that the admin is 
        // trying to disable a user with the role of admin or super admin.
        if ( ids.contains(userId) ) {
            // So, this operation is not allowed.
            return ResponseEntity.notFound().build();
        }

        Optional<User> optionalUser = service.disableEnableUser(userId);

        // If the user is present, it means the operation to disable or enable the user was successful.
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            AdminDto adminDto = new AdminDto(user.getId(), user.getUsername(), user.isEnabled());
            return ResponseEntity.ok(adminDto);
        }
        
        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

}
