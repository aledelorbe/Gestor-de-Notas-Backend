package com.alejandro.gestordenotas.controllers;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.security.Access;
import com.alejandro.gestordenotas.services.NoteService;
import com.alejandro.gestordenotas.services.UserService;
import com.alejandro.gestordenotas.utils.UtilValidation;

import jakarta.validation.Valid;


@RestController // To create a api rest.
@RequestMapping("/api/users") // To create a base path.
public class NoteController {

    // To Inject the service dependency
    @Autowired
    private UserService userService;

    // To Inject the service dependency
    @Autowired
    private NoteService service;
    
    @Autowired
    private UtilValidation utilValidation;

    @Autowired
    private Access access;

    // -----------------------------
    // Methods for note entity
    // -----------------------------

    // To create an endpoint that allows invoking the method 'getNotesByUserId'.
    @GetMapping("/{userId}/notes")
    public ResponseEntity<?> getNotesByUserId(@PathVariable Long userId, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and if it's present then return it.
        Optional<User> optionalUser = userService.findById(userId);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(userService.getNotesByUserId(userId));
        }
        // Else returns code response 404
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows saving a new note of an certain user
    @PostMapping("/{userId}/notes")
    public ResponseEntity<?> saveNewNoteByUserId(@Valid @RequestBody Note newNote, BindingResult result,
            @PathVariable Long userId, Principal principal) {
        // To handle of obligations of object attributes
        if (result.hasFieldErrors()) {
            return utilValidation.validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user if it exists then save the note
        Optional<User> optionalUser = userService.findById(userId);

        if (optionalUser.isPresent()) {
            User newUser = userService.saveNoteByUserId(optionalUser.get(), newNote);
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
            return utilValidation.validation(result);
        }

        // Check if the user that wants to access the resource is the owner
        if (!access.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and specific note and if they are present then
        // edit the information about note
        Optional<User> optionalUser = userService.findById(userId);
        Optional<Note> optionalNote = service.findById(noteId);

        if (optionalUser.isPresent() && optionalNote.isPresent()) {
            Optional<User> optionalUpdateUser = userService.editNoteByUserId(optionalUser.get(), noteId, editNote);

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
        if (!access.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user and specific note and if they are present then
        // delete a note
        Optional<User> optionalUser = userService.findById(userId);
        Optional<Note> optionalNote = service.findById(noteId);

        if (optionalUser.isPresent() && optionalNote.isPresent()) {
            Optional<User> optionalUpdateUser = userService.deleteNoteByUserId(optionalUser.get(), noteId);

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

}
