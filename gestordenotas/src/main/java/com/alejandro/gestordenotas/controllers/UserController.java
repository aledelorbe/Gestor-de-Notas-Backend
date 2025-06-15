package com.alejandro.gestordenotas.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.security.Access;
import com.alejandro.gestordenotas.services.UserService;
import com.alejandro.gestordenotas.utils.UtilValidation;

import jakarta.validation.Valid;

@RestController // To create a api rest.
@RequestMapping("/api/users") // To create a base path.
public class UserController {

    // To Inject the service dependency
    @Autowired
    private UserService service;

    @Autowired
    private UtilValidation utilValidation;

    @Autowired
    private Access access;

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // Endpoints for user role ----------------------

    // To create an endpoint that allows invoking the method findAll.
    @GetMapping()
    public List<User> users() {
        return service.findAll();
    }

    // To create an endpoint that allows invoking the method findById.
    @GetMapping("/{id}")
    public ResponseEntity<?> user(@PathVariable Long id, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(id, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return it.
        Optional<User> optionalUser = service.findById(id);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.orElseThrow());
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the method save user (the user
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
        User newUser = service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // To create an endpoint that allows invoking the method save user, but the user
    // will not become an admin
    // The annotation called 'RequestBody' allows receiving data of a user
    @PostMapping("/register")
    public ResponseEntity<?> saveNewUser(@Valid @RequestBody User user, BindingResult result) {
        // To handle the obligations of object attributes
        if (result.hasFieldErrors()) {
            return utilValidation.validation(result);
        }

        user.setAdmin(false);
        // When a new user is created to respond return the same user
        User newUser = service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // To create an endpoint that allows updating all of the values
    // of a specific user based its id.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id,
            Principal principal) {
        // To handle of obligations of object attributes
        if (result.hasFieldErrors()) {
            return utilValidation.validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(id, principal)) {

            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return specific user
        Optional<User> optionalUser = service.update(id, user);

        if (optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalUser.orElseThrow());
        }
        // Else return code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows deleting a specific user based its id.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(id, principal)) {

            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Find specific user and if it's present then return specific user
        Optional<User> optionalUser = service.deleteById(id);

        // Todo: when the object user has a jsonIgnore
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok().build();
        }
        // Else return code response 404
        return ResponseEntity.notFound().build();
    }

}