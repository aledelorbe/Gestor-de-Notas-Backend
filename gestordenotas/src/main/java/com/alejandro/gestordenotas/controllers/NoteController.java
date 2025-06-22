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


    // -----------------------------
    // Methods for note entity
    // -----------------------------

    // To create an endpoint that allows invoking the 'getNotesByUserId' method.
    @GetMapping("/{userId}/notes")
    public ResponseEntity<?> getNotesByUserId(@PathVariable Long userId, Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!userService.isOwner(userId, principal)) {
            // return a 404 status code.
            return ResponseEntity.notFound().build();
        }

        // Search for a specific user
        Optional<User> optionalUser = userService.findById(userId);

        // if the user is present then return the note array.
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get().getNotes());
        }

        // Else, return a 404 status code.
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

        // Call the 'saveNoteByUser' method
        Optional<User> optionalNewUser = service.saveNoteByUser(userId, newNote);

        // if the user is present then it means that the object could be saved
        if (optionalNewUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalNewUser.get());
        }

        // Else, return a 404 status code.
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
        if (!userService.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Call the 'editNoteByUser' method
        Optional<User> optionalUpdateUser = service.editNoteByUser(userId, noteId, editNote);

        // if the user is present then it means that the object could be updated
        if ( optionalUpdateUser.isPresent() ) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalUpdateUser.get());
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

    // To create an endpoint that allows deleting a certain note of a certain user
    @DeleteMapping("/{userId}/notes/{noteId}")
    public ResponseEntity<?> deleteNoteByUserId(@PathVariable Long userId, @PathVariable Long noteId,
            Principal principal) {

        // Check if the user that wants to access the resource is the owner
        if (!userService.isOwner(userId, principal)) {
            // return code response 404
            return ResponseEntity.notFound().build();
        }

        // Call the 'deleteNoteByUser' method
        Optional<User> optionalUpdateUser = service.deleteNoteByUser(userId, noteId);

        // if the user is present then it means that the object could be deleted
        if ( optionalUpdateUser.isPresent() ) {
            return ResponseEntity.status(HttpStatus.OK).body(optionalUpdateUser.get());
        }

        // Else, return a 404 status code.
        return ResponseEntity.notFound().build();
    }

}
