package com.alejandro.gestordenotas.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.data.RoleData;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.RoleRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;


@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    
    // To create a mock
    @Mock
    UserRepository repository; 

    // To create a mock
    @Mock
    RoleRepository roleRepository; 

    // To create a service object with the injection of a mock
    @InjectMocks
    UserServiceImp service;

    @Mock
    Principal principal;

    @Mock
    PasswordEncoder passwordEncoder;

    
    // To test the 'findById' method when we use an existing id
    @Test
    void findByIdExistingIdTest() {

        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser004()));

        // when
        Optional<User> optionalUser = service.findById(4L);

        // then
        assertNotNull(optionalUser.get());
        assertEquals(4L, optionalUser.get().getId());
        assertEquals("jorge", optionalUser.get().getUsername());
        
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'findById' method when we use an inexisting id
    @Test
    void findByIdInexistingIdTest() {

        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Optional<User> optionalUser2 = service.findById(11L);

        // then
        assertFalse(optionalUser2.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser2.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'save' method when the user was be saved with the user and admin role
    @Test
    void saveUserWithUserAndAdminRoleTest() {

        // Given
        String userRoleToSearch = "ROLE_USER";
        User userInsert = new User(null, "Javier", "Javier123");
        userInsert.setAdmin(true);
        String adminRoleToSearch = "ROLE_ADMIN";
        when(roleRepository.findByName(eq(userRoleToSearch))).thenReturn(Optional.of(RoleData.createRole001()));
        when(roleRepository.findByName(eq(adminRoleToSearch))).thenReturn(Optional.of(RoleData.createRole002()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("Javier123Encrypted");

        // when
        User newUser = service.save(userInsert);
        
        // then
        assertEquals("Javier", newUser.getUsername());
        assertEquals("Javier123Encrypted", newUser.getPassword());
        
        assertEquals(2, newUser.getRoles().size());

        verify(roleRepository, times(2)).findByName(any(String.class));
        verify(passwordEncoder).encode(anyString());
        verify(repository).save(any(User.class));
    }

    // To test the 'save' method when the user was be saved with the user role
    @Test
    void saveUserWithUserRoleTest() {

        // Given
        String userRoleToSearch = "ROLE_USER";
        User userInsert = new User(null, "Javier", "Javier123");
        userInsert.setAdmin(false);
        when(roleRepository.findByName(eq(userRoleToSearch))).thenReturn(Optional.of(RoleData.createRole001()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("Javier123Encrypted");

        // when
        User newUser = service.save(userInsert);
        
        // then
        assertEquals("Javier", newUser.getUsername());
        assertEquals("Javier123Encrypted", newUser.getPassword());
        
        assertEquals(1, newUser.getRoles().size());

        verify(roleRepository).findByName(any(String.class));
        verify(passwordEncoder).encode(anyString());
        verify(repository).save(any(User.class));
    }

    // To test the 'update' method when we use an existing id
    @Test
    void updateExistingIdTest() {

        // Given
        Long idToUpdate = 1L;
        User userToUpdate = new User(idToUpdate, "Angel", "Angel123");
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(passwordEncoder.encode(anyString())).thenReturn("Angel123Encrypted");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<User> result = service.update(idToUpdate, userToUpdate);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Angel", result.get().getUsername());
        assertEquals("Angel123Encrypted", result.get().getPassword());
        // The event is not possible to test. It might only be with an integration test.

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(passwordEncoder).encode(anyString());
        verify(repository).save(any(User.class));
    }

    // To test the 'update' method when we use an inexisting id
    @Test
    void updateInexistingIdTest() {

        // Given
        Long idToUpdate = 999999L;
        User userToUpdate = new User(idToUpdate, "Angel", "Angel123");
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> result2 = service.update(idToUpdate, userToUpdate);

        // Then
        assertFalse(result2.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            result2.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any(User.class));
    }

    // To test the 'delete' method when we use an existing id
    @Test
    void deleteExistingIdTest() {

        // Given
        Long idToDelete = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        Optional<User> result = service.deleteById(idToDelete);

        // Then
        assertTrue(result.isPresent());
        assertEquals("alejandro", result.get().getUsername());
        assertEquals("ale123", result.get().getPassword());

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).deleteById(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'delete' method when we use an inexisting id
    @Test
    void deleteInexistingIdTest() {

        // Given
        Long idToDelete = 9999999L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> result = service.deleteById(idToDelete);

        // Then
        assertFalse(result.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            result.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(repository, never()).deleteById(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // Aux Methods ---------------------------------------------

    // To test the 'isOwner' method when the user is the owner of resource
    @Test
    void isOwnerSuccessTest() {

        // Given
        Long userId = 1L;
        String username = "alejandro";
        when(principal.getName()).thenReturn(username);
        when(repository.findById(userId)).thenReturn(Optional.of(UserData.createUser001()));

        // When
        boolean result = service.isOwner(userId, principal);

        // Then
        assertTrue(result);

        verify(principal).getName();
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'isOwner' method when the user doesn't exist
    @Test
    void isOwnerInexistingIdTest() {

        // Given
        Long userId = 999999L;
        String username = "alejandro";
        when(principal.getName()).thenReturn(username);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When
        boolean result = service.isOwner(userId, principal);

        // Then
        assertFalse(result);

        verify(principal).getName();
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'isOwner' method when the user doesn't exist
    @Test
    void isOwnerNoOwnerTest() {

        // Given
        Long userId = 2L;
        String username = "alejandro";
        when(principal.getName()).thenReturn(username);
        when(repository.findById(userId)).thenReturn(Optional.of(UserData.createUser002()));

        // When
        boolean result = service.isOwner(userId, principal);

        // Then
        assertFalse(result);

        verify(principal).getName();
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
    }

}