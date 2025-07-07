package com.alejandro.gestordenotas.controllers;

import java.security.Principal;
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

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;
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

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // Endpoints for user role ----------------------

    // To create an endpoint that allows invoking the method findById.
    @GetMapping("/{id}")
    public ResponseEntity<?> user(@PathVariable Long id, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!service.isOwner(id, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return it.
        Optional<User> optionalUser = service.findById(id);

        if (optionalUser.isPresent()) {
            UserDto userDto = new UserDto();
            userDto.setId(optionalUser.get().getId());
            userDto.setUsername(optionalUser.get().getUsername());

            return ResponseEntity.ok(userDto);
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the 'saveUser' method, but the user
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

        UserDto userDto = new UserDto();
        userDto.setId(newUser.getId());
        userDto.setUsername(newUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
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
        if (!service.isOwner(id, principal)) {
            // return a 404 status code.
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return specific user
        Optional<User> optionalUser = service.update(id, user);

        if (optionalUser.isPresent()) {
            UserDto userDto = new UserDto();
            userDto.setId(optionalUser.get().getId());
            userDto.setUsername(optionalUser.get().getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows deleting a specific user based its id.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!service.isOwner(id, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Find specific user and if it's present then return specific user
        Optional<User> optionalUser = service.deleteById(id);

        if (optionalUser.isPresent()) {
            UserDto userDto = new UserDto();
            userDto.setId(optionalUser.get().getId());
            userDto.setUsername(optionalUser.get().getUsername());

            return ResponseEntity.ok(userDto);
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

}