package com.alejandro.gestordenotas.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.data.RoleData;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.RoleRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;


@ExtendWith(MockitoExtension.class)
class SuperAdminServiceImpTest {
    
    // To create a mock
    @Mock
    UserRepository repository; 

    // To create a service object with the injection of a mock
    @InjectMocks
    SuperAdminServiceImp service;

    // To create a mock
    @Mock
    RoleRepository roleRepository; 


    // To test the 'isSuperAdmin' method when the user has the super admin role
    @Test
    void isSuperAdminHasSuperAdminRoleTest() {

        // Given
        Long userIdToSearch = 1L;
        when(repository.getIdOfSuperAdmin()).thenReturn(userIdToSearch);

        // when
        boolean result = service.isSuperAdmin(userIdToSearch);

        // Then
        assertTrue(result);

        verify(repository).getIdOfSuperAdmin();
    }

    // To test the 'isSuperAdmin' method when the user has no the super admin role
    @Test
    void isSuperAdminHasNoSuperAdminRoleTest() {

        // Given
        Long userIdToSearch = 1L;
        when(repository.getIdOfSuperAdmin()).thenReturn(19L);

        // when
        boolean result = service.isSuperAdmin(userIdToSearch);

        // Then
        assertFalse(result);

        verify(repository).getIdOfSuperAdmin();
    }

    // To test the 'addRemoveAdminRoleFromUser' method when we use an inexisting user id
    @Test
    void addRemoveAdminRoleFromUserInexistingUserIdTest() {

        // Given
        Long userIdToSearch = 99999L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Optional<User> optionalUser = service.addRemoveAdminRoleFromUser(userIdToSearch);

        // then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(roleRepository, never()).findByName(anyString());
        verify(repository, never()).save(any(User.class));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when we use an existing user id but the admin role doesnt exist
    @Test
    void addRemoveAdminRoleFromUserInexistingAdminRoleTest() {

        // Given
        Long userIdToSearch = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // when
        Optional<User> optionalUser = service.addRemoveAdminRoleFromUser(userIdToSearch);

        // then
        assertFalse(optionalUser.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(roleRepository).findByName(anyString());
        verify(repository, never()).save(any(User.class));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when the admin role was removed from the user
    @Test
    void addRemoveAdminRoleFromUserRemoveAdminRoleTest() {

        // Given
        Long userIdToSearch = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(RoleData.createRole002()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<User> optionalUser = service.addRemoveAdminRoleFromUser(userIdToSearch);

        // then
        assertTrue(optionalUser.isPresent());
        assertEquals(1L, optionalUser.get().getId());
        assertEquals("alejandro", optionalUser.get().getUsername());
        assertFalse(optionalUser.get().isAdmin());

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(roleRepository).findByName(anyString());
        verify(repository).save(any(User.class));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when the admin role was added to the user
    @Test
    void addRemoveAdminRoleFromUserAddAdminRoleTest() {

        // Given
        Long userIdToSearch = 6L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser006()));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(RoleData.createRole002()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<User> optionalUser = service.addRemoveAdminRoleFromUser(userIdToSearch);

        // then
        assertTrue(optionalUser.isPresent());
        assertEquals(6L, optionalUser.get().getId());
        assertEquals("pancha", optionalUser.get().getUsername());
        assertTrue(optionalUser.get().isAdmin());

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(roleRepository).findByName(anyString());
        verify(repository).save(any(User.class));
    }

}
