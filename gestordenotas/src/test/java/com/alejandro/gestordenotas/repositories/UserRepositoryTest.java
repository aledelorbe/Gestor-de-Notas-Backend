package com.alejandro.gestordenotas.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.alejandro.gestordenotas.dto.AdminDto;
import com.alejandro.gestordenotas.dto.SuperAdminDto;


// To load the beans related to the persist layer.
// To load/insert the data on the file 'insert.sql'  
// To use the configurations on application-test.properties
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/insert.sql") 
class UserRepositoryTest {
    
    @Autowired
    UserRepository repository;

    // To test the 'getAllUsersWithUserRole' method 
    @Test
    void getAllUsersWithUserRoleTest () {

        // When
        List<AdminDto> users = repository.getAllUsersWithUserRole();

        // Then
        assertFalse(users.isEmpty());
        assertEquals(3, users.size());

        assertEquals(4L, users.get(0).getId());
        assertEquals("jorge", users.get(0).getUsername());
        assertTrue(users.get(0).isEnabled());

        assertEquals(5L, users.get(1).getId());
        assertEquals("rayas", users.get(1).getUsername());
        assertTrue(users.get(1).isEnabled());
    }

    // To test the 'getUserWithUserRole' method 
    @Test
    void getUserWithUserRoleTest () {

        // Given
        Long userIdToSearch = 6L;

        // When
        Optional<AdminDto> optionalUser = repository.getUserWithUserRole(userIdToSearch);
        AdminDto user = optionalUser.get();

        // Then
        assertTrue(optionalUser.isPresent());

        assertEquals(6L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
    }

    // To test the 'getAllIdsWithAdminAndSuperAdminRole' method 
    @Test
    void getAllIdsWithAdminAndSuperAdminRoleTest () {

        // When
        List<Long> ids = repository.getAllIdsWithAdminAndSuperAdminRole();

        // Then
        assertFalse(ids.isEmpty());
        assertEquals(3, ids.size());

        assertEquals(1L, ids.get(0));
        assertEquals(2L, ids.get(1));
        assertEquals(3L, ids.get(2));
    }

    // To test the 'getAllUsersWithUserAndAdminRole' method 
    @Test
    void getAllUsersWithUserAndAdminRoleTest () {

        // When
        List<SuperAdminDto> users = repository.getAllUsersWithUserAndAdminRole();

        // Then
        assertFalse(users.isEmpty());
        assertEquals(5, users.size());

        assertEquals(2L, users.get(0).getId());
        assertEquals(3L, users.get(1).getId());
        assertEquals(4L, users.get(2).getId());
        assertEquals(5L, users.get(3).getId());
        assertEquals(6L, users.get(4).getId());

        assertEquals(4L, users.get(2).getId());
        assertEquals("jorge", users.get(2).getUsername());
        assertTrue(users.get(2).isEnabled());
        assertFalse(users.get(2).isAdmin());

        assertEquals(5L, users.get(3).getId());
        assertEquals("rayas", users.get(3).getUsername());
        assertTrue(users.get(3).isEnabled());
        assertFalse(users.get(3).isAdmin());
    }

    // To test the 'getUserWithUserAndAdminRole' method 
    @Test
    void getUserWithUserAndAdminRoleTest () {

        // Given
        Long userIdToSearch = 6L;

        // When
        Optional<SuperAdminDto> optionalUser = repository.getUserWithUserAndAdminRole(userIdToSearch);
        SuperAdminDto user = optionalUser.get();

        // Then
        assertTrue(optionalUser.isPresent());

        assertEquals(6L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        assertFalse(user.isAdmin());
    }

    // To test the 'getIdOfSuperAdmin' method 
    @Test
    void getIdOfSuperAdminTest () {

        // When
        Long superAdminId = repository.getIdOfSuperAdmin();

        // Then
        assertEquals(1L, superAdminId);
    }
    
}
