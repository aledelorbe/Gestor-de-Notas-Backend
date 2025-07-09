package com.alejandro.gestordenotas.integrations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

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
import com.alejandro.gestordenotas.dto.SuperAdminDto;


// To load/insert the data on the file 'insert.sql'  
// To use the configurations on application-test.properties
// To start the test context with a random port
@Sql(scripts = "/insert.sql") 
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SuperAdminIntegrationTest {
    
    // To inject the component of testRestTemplate
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    // getAllUsersWithUserAndAdminRole ----------------------------------------------------

    // To test the 'getAllUsersWithUserAndAdminRole' endpoint when is called by user with the super admin role
    @Test
    void getAllUsersWithUserAndAdminRoleCalledBySuperAdminIntegrationTest() {

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
        ResponseEntity<SuperAdminDto[]> response = client.exchange(
                "/api/super-admins/users-and-admins",
                HttpMethod.GET,
                entity,
                SuperAdminDto[].class
        );
        List<SuperAdminDto> users = Arrays.asList(response.getBody());  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(users.isEmpty());
        assertEquals(5, users.size());

        assertEquals(11L, users.get(0).getId());
        assertEquals(12L, users.get(1).getId());
        assertEquals(13L, users.get(2).getId());
        assertEquals(14L, users.get(3).getId());
        assertEquals(15L, users.get(4).getId());

        assertEquals(13L, users.get(2).getId());
        assertEquals("jorge", users.get(2).getUsername());
        assertTrue(users.get(2).isEnabled());
        assertFalse(users.get(2).isAdmin());

        assertEquals(14L, users.get(3).getId());
        assertEquals("rayas", users.get(3).getUsername());
        assertTrue(users.get(3).isEnabled());
        assertFalse(users.get(3).isAdmin());
    }

    // To test the 'getAllUsersWithUserAndAdminRole' endpoint when is called by user with the admin role
    @Test
    void getAllUsersWithUserAndAdminRoleCalledByAdminIntegrationTest() {

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
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/users-and-admins",
                HttpMethod.GET,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'getAllUsersWithUserAndAdminRole' endpoint when is called by user with the user role
    @Test
    void getAllUsersWithUserAndAdminRoleCalledByUserIntegrationTest() {

        // Given
        User admin = UserData.createUser004();
        admin.setId(13L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/users-and-admins",
                HttpMethod.GET,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // getUserWithUserAndAdminRole ----------------------------------------------------

    // To test the 'getUserWithUserAndAdminRole' endpoint when is called by user with the super admin role
    @Test
    void getUserWithUserAndAdminRoleCalledBySuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<SuperAdminDto> response = client.exchange(
                "/api/super-admins/user-and-admin/" + userIdToSearch,
                HttpMethod.GET,
                entity,
                SuperAdminDto.class
        );
        SuperAdminDto user = response.getBody();  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        assertEquals(15L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        assertFalse(user.isAdmin());
    }

    // To test the 'getUserWithUserAndAdminRole' endpoint when is called by user with the admin role
    @Test
    void getUserWithUserAndAdminRoleCalledByAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/user-and-admin/" + userIdToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'getUserWithUserAndAdminRole' endpoint when is called by user with the user role
    @Test
    void getUserWithUserAndAdminRoleCalledByUserIntegrationTest() {

        // Given
        User admin = UserData.createUser004();
        admin.setId(13L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/user-and-admin/" + userIdToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'getUserWithUserAndAdminRole' endpoint when is called by user with the super admin role
    // But the searched user doesnt exist
    @Test
    void getUserWithUserAndAdminRoleCalledBySuperAdminInexistingIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 999999L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/user-and-admin/" + userIdToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // addRemoveAdminRoleFromUser ----------------------------------------------------

    // To test the 'addRemoveAdminRoleFromUser' endpoint when is called by user with the super admin role
    @Test
    void addRemoveAdminRoleFromUserCalledBySuperAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<SuperAdminDto> response = client.exchange(
                "/api/super-admins/convert-user-into-admin/" + userIdToSearch,
                HttpMethod.PATCH,
                entity,
                SuperAdminDto.class
        );
        SuperAdminDto user = response.getBody();  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        assertEquals(15L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        assertTrue(user.isAdmin());
    }

    // To test the 'addRemoveAdminRoleFromUser' endpoint when is called by user with the admin role
    @Test
    void addRemoveAdminRoleFromUserCalledByAdminIntegrationTest() {

        // Given
        User admin = UserData.createUser002();
        admin.setId(11L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/convert-user-into-admin/" + userIdToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'addRemoveAdminRoleFromUser' endpoint when is called by user with the user role
    @Test
    void addRemoveAdminRoleFromUserCalledByUserIntegrationTest() {

        // Given
        User admin = UserData.createUser004();
        admin.setId(13L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 15L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/convert-user-into-admin/" + userIdToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'addRemoveAdminRoleFromUser' endpoint when is called by user with the super admin role
    // but the searched user doesnt exist
    @Test
    void addRemoveAdminRoleFromUserCalledBySuperAdminInexistingIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 999999L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/convert-user-into-admin/" + userIdToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'addRemoveAdminRoleFromUser' endpoint when is called by user with the super admin role
    // but the searched user has the super admin role
    @Test
    void addRemoveAdminRoleFromUserCalledBySuperAdminSuperIntegrationTest() {

        // Given
        User admin = UserData.createUser001();
        admin.setId(10L); // Change the user id for the user id in the insert.sql file
        Long userIdToSearch = 10L;

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(admin);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/super-admins/convert-user-into-admin/" + userIdToSearch,
                HttpMethod.PATCH,
                entity,
                Void.class
        );
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // superAdminEnableUser ----------------------------------------------------

    // To test the 'superAdminEnableUser' endpoint when is called by user with the super admin role
    @Test
    void disableEnableUserCalledBySuperAdminIntegrationTest() {
        // Given
        User superAdmin = UserData.createUser001();
        superAdmin.setId(10L); 
        Long userIdToModify = 15L;

        String token = jwtTokenUtil.createToken(superAdmin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<SuperAdminDto> response = client.exchange(
            "/api/super-admins/disable-enable-user/" + userIdToModify,
            HttpMethod.PATCH,
            entity,
            SuperAdminDto.class
        );
        SuperAdminDto user = response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isAdmin()); 
        assertTrue(user.isEnabled()); 
    }

    // To test the 'superAdminEnableUser' endpoint when is called by user with the admin role
    @Test
    void disableEnableUserCalledByAdminIntegrationTest() {
        // Given
        User admin = UserData.createUser002();
        admin.setId(11L);
        Long userIdToModify = 15L;

        String token = jwtTokenUtil.createToken(admin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
            "/api/super-admins/disable-enable-user/" + userIdToModify,
            HttpMethod.PATCH,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'superAdminEnableUser' endpoint when is called by user with the user role
    @Test
    void disableEnableUserCalledByUserIntegrationTest() {
        // Given
        User user = UserData.createUser004();
        user.setId(13L);
        Long userIdToModify = 15L;

        String token = jwtTokenUtil.createToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
            "/api/super-admins/disable-enable-user/" + userIdToModify,
            HttpMethod.PATCH,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'superAdminEnableUser' endpoint when is called by user with the super admin role
    // but the searched user doesnt exist
    @Test
    void disableEnableUserCalledBySuperAdminInexistingIntegrationTest() {
        // Given
        User superAdmin = UserData.createUser001();
        superAdmin.setId(10L);
        Long nonexistentUserId = 999999L;

        String token = jwtTokenUtil.createToken(superAdmin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
            "/api/super-admins/disable-enable-user/" + nonexistentUserId,
            HttpMethod.PATCH,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'superAdminEnableUser' endpoint when is called by user with the super admin role
    // but the searched user has the super admin role
    @Test
    void disableEnableUserCalledBySuperAdminSuperIntegrationTest() {
        // Given
        User superAdmin = UserData.createUser001();
        superAdmin.setId(10L);
        Long nonexistentUserId = 10L;

        String token = jwtTokenUtil.createToken(superAdmin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
            "/api/super-admins/disable-enable-user/" + nonexistentUserId,
            HttpMethod.PATCH,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}
