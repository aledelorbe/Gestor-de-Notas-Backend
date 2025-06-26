package com.alejandro.gestordenotas.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.UserRepository;


@ExtendWith(MockitoExtension.class)
class AdminServiceImpTest {
    
    // To create a mock
    @Mock
    UserRepository repository; 

    // To create a service object with the injection of a mock
    @InjectMocks
    AdminServiceImp service;


    // To test the 'disableEnableUser' method when the user was disabled
    @Test
    void disableEnableUserDisabledTest() {

        // Given
        Long userIdToSearch = 4L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser004()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<User> optionalUser = service.disableEnableUser(userIdToSearch);

        // then
        assertNotNull(optionalUser.get());
        assertEquals(4L, optionalUser.get().getId());
        assertEquals("jorge", optionalUser.get().getUsername());
        assertFalse(optionalUser.get().isEnabled());
        
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).save(any(User.class));
    }

    // To test the 'disableEnableUser' method when the user was enabled
    @Test
    void disableEnableUserEnabledTest() {

        // Given
        Long userIdToSearch = 6L;
        when(repository.findById(anyLong())).thenReturn(Optional.of(UserData.createUser006()));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<User> optionalUser = service.disableEnableUser(userIdToSearch);

        // then
        assertNotNull(optionalUser.get());
        assertEquals(6L, optionalUser.get().getId());
        assertEquals("pancha", optionalUser.get().getUsername());
        assertTrue(optionalUser.get().isEnabled());
        
        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(repository).save(any(User.class));
    }
    
    // To test the 'disableEnableUser' method when we use an inexisting user id
    @Test
    void disableEnableUserInexistingIdTest() {

        // Given
        Long userIdToSearch = 99999L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Optional<User> optionalUser2 = service.disableEnableUser(userIdToSearch);

        // then
        assertFalse(optionalUser2.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            optionalUser2.orElseThrow();
        });

        verify(repository).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(repository, never()).save(any(User.class));
    }

}
