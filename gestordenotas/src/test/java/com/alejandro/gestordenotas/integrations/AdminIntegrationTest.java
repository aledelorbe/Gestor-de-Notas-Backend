package com.alejandro.gestordenotas.integrations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.utils.JwtTokenUtil;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.dto.AdminDto;


// To load/insert the data on the file 'insert.sql'  
// To use the configurations on application-test.properties
// To start the test context with a random port
@Sql(scripts = "/insert.sql") 
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminIntegrationTest {
    
    // To inject the component of testRestTemplate
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    
    // getAllUsersWithUserRole ----------------------------------------------------

    // To test the 'getAllUsersWithUserRole' endpoint when is called by user with the super admin role
    @Test
    void getAllUsersWithUserRoleCalledBySuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto[]> response = client.exchange(
                "/api/admins/users",
                HttpMethod.GET,
                entity,
                AdminDto[].class
        );
        List<AdminDto> users = Arrays.asList(response.getBody());  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(users.isEmpty());
        assertEquals(3, users.size());
        assertEquals(13, users.get(0).getId());
        assertEquals("jorge", users.get(0).getUsername());
        assertTrue(users.get(0).isEnabled());
    }

    // To test the 'getAllUsersWithUserRole' endpoint when is called by user with the admin role
    @Test
    void getAllUsersWithUserRoleCalledByAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto[]> response = client.exchange(
                "/api/admins/users",
                HttpMethod.GET,
                entity,
                AdminDto[].class
        );
        List<AdminDto> users = Arrays.asList(response.getBody());  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(users.isEmpty());
        assertEquals(3, users.size());
        assertEquals(13, users.get(0).getId());
        assertEquals("jorge", users.get(0).getUsername());
        assertTrue(users.get(0).isEnabled());
    }

    // To test the 'getAllUsersWithUserRole' endpoint when is called by user with the user role
    @Test
    void getAllUsersWithUserRoleCalledByUserIntegrationTest() {

        // Given
        User user = UserData.createUser004();
        user.setId(13L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(user);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins/users",
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // getUserWithUserRole ----------------------------------------------------
    
    // To test the 'getUserWithUserRole' endpoint when is called by user with the super admin role
    @Test
    void getUserWithUserRoleCalledBySuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long idUserToSearch = 14L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto> response = client.exchange(
                "/api/admins/user/" + idUserToSearch,
                HttpMethod.GET,
                entity,
                AdminDto.class
        );
        AdminDto user = response.getBody();  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(14, user.getId());
        assertEquals("rayas", user.getUsername());
        assertTrue(user.isEnabled());
    }

    // To test the 'getUserWithUserRole' endpoint when is called by user with the admin role
    @Test
    void getUserWithUserRoleCalledByAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // Change the user id for the user id in the insert.sql file
        Long idUserToSearch = 14L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto> response = client.exchange(
                "/api/admins/user/" + idUserToSearch,
                HttpMethod.GET,
                entity,
                AdminDto.class
        );
        AdminDto user = response.getBody();  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(14, user.getId());
        assertEquals("rayas", user.getUsername());
        assertTrue(user.isEnabled());
    }

    // To test the 'getUserWithUserRole' endpoint when is called by user with the user role
    @Test
    void getUserWithUserRoleCalledByUserIntegrationTest() {

        // Given
        User admin = UserData.createUser005();
        admin.setId(14L); // Change the user id for the user id in the insert.sql file
        Long idUserToSearch = 14L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins/user/" + idUserToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'getUserWithUserRole' endpoint when is called by user with the admin role
    // But the searched user does not exist
    @Test
    void getUserWithUserRoleCalledByAdminInexistingUserIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // Change the user id for the user id in the insert.sql file
        Long idUserToSearch = 999999L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins/user/" + idUserToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // saveNewUserAdmin ----------------------------------------------------

    // Register a new admin user when this endpoint is called by user with the super admin role
    @Test
    void saveNewUserAdminSuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // ID que existe en insert.sql

        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "nuevo");
        userInsert.put("password", "nuevo123.");

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userInsert, headers);

        // When
        ResponseEntity<User> response = client.exchange(
                "/api/admins",
                HttpMethod.POST,
                entity,
                User.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("nuevo", response.getBody().getUsername());
        assertTrue(response.getBody().isAdmin());
    }

    // Register a new admin user when this endpoint is called by user with the admin role
    @Test
    void saveNewUserAdminAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // ID que existe en insert.sql

        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "nuevo");
        userInsert.put("password", "nuevo123.");

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userInsert, headers);

        // When
        ResponseEntity<User> response = client.exchange(
                "/api/admins",
                HttpMethod.POST,
                entity,
                User.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("nuevo", response.getBody().getUsername());
        assertTrue(response.getBody().isAdmin());
    }

    // Register a new admin user when this endpoint is called by user with the user role
    @Test
    void saveNewUserAdminUserIntegrationTest() {

        // Given
        User admin = UserData.createUser005();
        admin.setId(14L); // ID que existe en insert.sql

        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "nuevo");
        userInsert.put("password", "nuevo123.");

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userInsert, headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins",
                HttpMethod.POST,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // disableEnableUser ----------------------------------------------------

    //  Disable or enable a user when this endpoint is called by a user with the super admin role
    @Test
    void disableEnableUserSuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // ID que existe en insert.sql
        Long idToSearch = 15L;

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto> response = client.exchange(
                "/api/admins/user/" + idToSearch,
                HttpMethod.PATCH,
                entity,
                AdminDto.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("pancha", response.getBody().getUsername());
        assertTrue(response.getBody().isEnabled());
    }

    //  Disable or enable a user when this endpoint is called by a user with the admin role
    @Test
    void disableEnableUserAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // ID que existe en insert.sql
        Long idToSearch = 15L;

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<AdminDto> response = client.exchange(
                "/api/admins/user/" + idToSearch,
                HttpMethod.PATCH,
                entity,
                AdminDto.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("pancha", response.getBody().getUsername());
        assertTrue(response.getBody().isEnabled());
    }

    //  Disable or enable a user when this endpoint is called by a user with the user role
    @Test
    void disableEnableUserUserIntegrationTest() {

        // Given
        User admin = UserData.createUser005();
        admin.setId(15L); // ID que existe en insert.sql
        Long idToSearch = 15L;

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins/user/" + idToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    //  Disable or enable a user when this endpoint is called by a user with the admin role
    // but the searched user doesnt exist
    @Test
    void disableEnableUserAdminInexistingIdIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // ID que existe en insert.sql
        Long idToSearch = 9999L;

        // Generate a valid token for this user (admin)
        String token = jwtTokenUtil.createToken(admin);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/admins/user/" + idToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}
