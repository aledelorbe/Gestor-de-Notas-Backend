package com.alejandro.gestordenotas.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alejandro.gestordenotas.data.NoteData;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.repositories.UserRepository;
import com.alejandro.gestordenotas.repositories.NoteRepository;


@ExtendWith(MockitoExtension.class)
class NoteServiceImpTest {
    
    // To create a mock
    @Mock
    NoteRepository repository; 

    // To create a mock
    @Mock
    UserRepository userRepository; 

    // To create a service object with the injection of a mock
    @InjectMocks
    NoteServiceImp service;

    // To create a service object with the injection of a mock
    @InjectMocks
    UserServiceImp userService;


    // To test the 'findById' method when an existent user ID is used
    @Test
    void findByIdExistingIdTest() {

        // Given
        Long idToSearch = 5L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote005()));

        // when
        Optional<Note> optionalNote = service.findById(idToSearch);

        // then
        assertNotNull(optionalNote.get());
        assertEquals(5L, optionalNote.get().getId());
        assertEquals("This is the note No. 5", optionalNote.get().getContent());
        
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
    }

    // To test the 'findById' method when a non-existent user ID is used
    @Test
    void findByIdInexistingIdTest() {

        // Given
        Long idToSearch = 99999L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Optional<Note> optionalNote2 = service.findById(idToSearch);

        // then
        assertFalse(optionalNote2.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalNote2.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, false)));
    }

    // To test the 'saveNoteByUser' method when an existent user ID is used
    @Test
    void saveNoteByUserExistingIdTest() {
    
        // Given
        Long idToSearch = 4L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser004()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Note noteInsert = new Note(null, "this is a new note");
        
        // when
        Optional<User> optionalNewUser = service.saveNoteByUser(idToSearch, noteInsert);
        
        // then
        User newUser = optionalNewUser.get();
        int size = newUser.getNotes().size();
        List<Note> notes = newUser.getNotes();

        assertNotNull(newUser);
        assertEquals(4L, newUser.getId());
        assertEquals("jorge", newUser.getUsername());

        assertNotNull(notes);
        assertEquals(3, size);
        assertEquals("this is a new note", notes.get(size - 1).getContent());

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(userRepository).save(any(User.class));
    }

    // To test the 'saveNoteByUser' method a non-existent user ID is used
    @Test
    void saveNoteByUserInexistingIdTest() {
    
        // Given
        Long idToSearch = 99999L;
        Note noteInsert = new Note(null, "this is a new note");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // when
        Optional<User> optionalNewUser = service.saveNoteByUser(idToSearch, noteInsert);
        
        // Then
        assertFalse(optionalNewUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalNewUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'editNoteByUser' method when a non-existent user ID is used
    @Test
    void editNoteByUserNoneExistingUserIdTest() {
        
        // Given
        Long idToSearch = 9999L;
        Long noteIdToSearch = 10L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote001()));
        Note noteToUpdate = new Note(null, "this is a updated note");
        
        // When
        Optional<User> optionalUser = service.editNoteByUser(idToSearch, noteIdToSearch, noteToUpdate);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'editNoteByUser' method when a non-existent note ID is used
    @Test
    void editNoteByUserNoneExistingNoteIdTest() {
        
        // Given
        Long idToSearch = 1L;
        Long noteIdToSearch = 9999L;
        Note noteToUpdate = new Note(null, "this is a updated note");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<User> optionalUser = service.editNoteByUser(idToSearch, noteIdToSearch, noteToUpdate);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, false)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'editNoteByUser' method when the existing note ID and user ID are used but the user is not an owner
    @Test
    void editNoteByUserNoOwnerTest() {
        
        // Given
        Long idToSearch = 1L;
        Long noteIdToSearch = 8L;
        Note noteToUpdate = new Note(null, "this is a updated note");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote008()));
        
        // When
        Optional<User> optionalUser = service.editNoteByUser(idToSearch, noteIdToSearch, noteToUpdate);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository, never()).save(any(User.class));
    }

    // To test the 'editNoteByUser' method when the existing note ID and user ID are used but the user is an owner
    @Test
    void editNoteByUserExistingIdTest() {
        
        // Given
        Long idToSearch = 4L;
        Long noteIdToSearch = 8L;
        Note noteToUpdate = new Note(null, "this is a updated note");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser004()));
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote008()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<User> optionalUser = service.editNoteByUser(idToSearch, noteIdToSearch, noteToUpdate);

        // then
        User newUserDb = optionalUser.get();

        assertNotNull(newUserDb);
        assertEquals(4L, newUserDb.getId());
        assertEquals("jorge", newUserDb.getUsername());

        Note noteUpdated = newUserDb.getNotes().get(1);

        assertEquals("this is a updated note", noteUpdated.getContent());

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository).save(any(User.class));
    }

    // Test the 'deleteNoteByUser' method when a non-existent user ID is used
    @Test
    void deleteNoteByUserNoneExistingUserIdTest() {
        
        // Given
        Long idToSearch = 9999L;
        Long noteIdToSearch = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote001()));
        
        // When
        Optional<User> optionalUser = service.deleteNoteByUser(idToSearch, noteIdToSearch);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'deleteNoteByUser' method when a non-existent note ID is used
    @Test
    void deleteNoteByUserNoneExistingNoteIdTest() {
        
        // Given
        Long idToSearch = 1L;
        Long noteIdToSearch = 9999L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<User> optionalUser = service.deleteNoteByUser(idToSearch, noteIdToSearch);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, false)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'deleteNoteByUser' method when the existing note ID and user ID are used but the user is not an owner
    @Test
    void deleteNoteByUserNoOwnerTest() {
        
        // Given
        Long idToSearch = 1L;
        Long noteIdToSearch = 8L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote008()));
        
        // When
        Optional<User> optionalUser = service.deleteNoteByUser(idToSearch, noteIdToSearch);

        // Then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(userRepository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository, never()).save(any(User.class));
    }

    // Test the 'deleteNoteByUser' method when the existing note ID and user ID are used and the user is an owner
    @Test
    void deleteNoteByUserOwnerTest() {
        
        // Given
        Long idToSearch = 4L;
        Long noteIdToSearch = 8L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser004()));
        when(repository.findById(anyLong())).thenReturn(Optional.of(NoteData.createNote008()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when: get notes (first time)
        List<Note> notes = userRepository.findById(idToSearch).get().getNotes();

        // then (first time)
        assertNotNull(notes);
        assertEquals(2, notes.size());

        // when: Delete a note and get notes (second time)
        Optional<User> optionalUser2 = service.deleteNoteByUser(idToSearch, noteIdToSearch);
        List<Note> notes2 = userRepository.findById(idToSearch).get().getNotes();

        // then
        assertNotNull(notes2);
        assertEquals(1, notes2.size());

        User newUserDb = optionalUser2.get();

        assertNotNull(newUserDb);
        assertEquals(4L, newUserDb.getId());
        assertEquals("jorge", newUserDb.getUsername());

        verify(userRepository, times(3)).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).findById(argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userRepository).save(any(User.class));
    }

}