package com.alejandro.gestordenotas.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.NoteService;
import com.alejandro.gestordenotas.services.UserService;
import com.alejandro.gestordenotas.services.dto.UserDto;

import jakarta.validation.Valid;

@RestController // To create a api rest.
@RequestMapping("/api/users") // To create a base path.
public class UserController {

    // To Inject the service dependency
    @Autowired
    private UserService service;

    // To Inject the service dependency
    @Autowired
    private NoteService noteService;

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // Endpoint's for user role ----------------------

    // To create an endpoint that allows invoking the method findAll.
    @GetMapping()
    public List<User> users() {
        return service.findAll();
    }

    // To create an endpoint that allows invoking the method findById.
    @GetMapping("/{id}")
    public ResponseEntity<?> user(@PathVariable Long id, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(id, principal)) {
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
            return validation(result);
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
            return validation(result);
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
            return validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(id, principal)) {

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
        if (!isOwner(id, principal)) {

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

    // Endpoint's for admin role ----------------------
    
    // To create an endpoint that allows invoking the method
    // 'getAllUsersWithRoleUser'.
    @GetMapping("/admin")
    public ResponseEntity<?> getUsersWithRoleUser() {
        return ResponseEntity.ok(service.getAllUsersWithRoleUser());
    }
    
    // To create an endpoint that allows invoking the method
    // 'getUserWithRoleUser'.
    @GetMapping("/admin/{userId}")
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
    @PatchMapping("/admin/{userId}")
    public ResponseEntity<?> enableUser(@PathVariable Long userId) {

        List<Long> ids = service.getAllIdWithRoleAdminAndSuperAdmin();

        // If the list of id's contains the 'userId' it means that the admin user
        // wants to delete a user with role admin or super admin.
        if (ids.contains(userId)) {
            // Else returns code response 404
            return ResponseEntity.notFound().build();
        }

        return superAdminEnableUser(userId);
    }
    
    // Endpoint's for super admin role ----------------------

    // To create an endpoint that allows invoking the method
    // 'getAllUsersWithRoleUser'.
    @GetMapping("/super-admin")
    public ResponseEntity<?> getUsersAndAdmins() {
        return ResponseEntity.ok(service.getAllUsersWithRoleUserAndAdmin());
    }

    // To create an endpoint that allows converting a user into an administrator
    // user
    @PatchMapping("/super-admin/convert-user/{userId}")
    public ResponseEntity<?> convertUserIntoAdmin(@PathVariable Long userId) {

        // Search for a specific user
        Optional<User> optionalUser = service.findById(userId);

        // If this user is present then the user is converted into an admin user
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(service.convertUserIntoAdmin(optionalUser.get()));
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows invoking the method
    // 'enableUser'.
    @PatchMapping("/super-admin/enabled-user/{userId}")
    public ResponseEntity<?> superAdminEnableUser(@PathVariable Long userId) {

        // Search for a specific user and if it's present then return it.
        Optional<User> optionalUser = service.findById(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(service.enabledUser(optionalUser.get()));
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }


    // -----------------------------
    // Methods for note entity
    // -----------------------------

    // To create an endpoint that allows saving a new note of an certain user
    @PostMapping("/{userId}/notes")
    public ResponseEntity<?> saveNewNoteByUserId(@Valid @RequestBody Note newNote, BindingResult result,
            @PathVariable Long userId, Principal principal) {
        // To handle of obligations of object attributes
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user if it exists then save the note
        Optional<User> optionalUser = service.findById(userId);

        if (optionalUser.isPresent()) {
            User newUser = service.saveNoteByUserId(optionalUser.get(), newNote);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows updating information of a certain note of a
    // certain user
    @PatchMapping("/{userId}/notes/{noteId}")
    public ResponseEntity<?> editNoteByUserId(@Valid @RequestBody Note editNote, BindingResult result,
            @PathVariable Long userId, @PathVariable Long noteId, Principal principal) {

        // To handle of obligations of object attributes
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and specific note and if they are present then
        // edit the information about note
        Optional<User> optionalUser = service.findById(userId);
        Optional<Note> optionalNote = noteService.findById(noteId);

        if (optionalUser.isPresent() && optionalNote.isPresent()) {
            Optional<User> optionalUpdateUser = service.editNoteByUserId(optionalUser.get(), noteId, editNote);

            // If the 'Update Optional User' option is present, it means that the note could
            // be updated.
            if (optionalUpdateUser.isPresent()) {
                User updateUser = optionalUpdateUser.get();

                return ResponseEntity.status(HttpStatus.CREATED).body(updateUser);
            } else {
                // Else returns code response 404
                return ResponseEntity.notFound().build();
            }
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows deleting a certain note of a certain user
    @DeleteMapping("/{userId}/notes/{noteId}")
    public ResponseEntity<?> deleteNoteByUserId(@PathVariable Long userId, @PathVariable Long noteId,
            Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and specific note and if they are present then
        // delete a note
        Optional<User> optionalUser = service.findById(userId);
        Optional<Note> optionalNote = noteService.findById(noteId);

        if (optionalUser.isPresent() && optionalNote.isPresent()) {
            Optional<User> optionalUpdateUser = service.deleteNoteByUserId(optionalUser.get(), noteId);

            // If the 'Update Optional User' option is present, it means that the note could
            // be updated.
            if (optionalUpdateUser.isPresent()) {
                User updateUser = optionalUpdateUser.get();

                return ResponseEntity.ok(updateUser);
            } else {
                // Else returns code response 404
                return ResponseEntity.notFound().build();
            }
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    // To create an endpoint that allows invoking the method 'getNotesByUserId'.
    @GetMapping("/{userId}/notes")
    public ResponseEntity<?> getNotesByUserId(@PathVariable Long userId, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return it.
        Optional<User> optionalUser = service.findById(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(service.getNotesByUserId(userId));
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // -----------------------------
    // Method to validate
    // -----------------------------

    // To send a JSON object with messages about the obligations of each object
    // attribute
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(e -> {
            errors.put(e.getField(), "El campo " + e.getField() + " " + e.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // To know if the user who wants to access the resource is the owner or not
    private boolean isOwner(Long id, Principal principal) {
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