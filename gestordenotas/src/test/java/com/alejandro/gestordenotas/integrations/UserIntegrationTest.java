package com.alejandro.gestordenotas.integrations;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
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
import com.alejandro.gestordenotas.dto.LoginErrorResponseDto;
import com.alejandro.gestordenotas.dto.LoginResponseDto;
import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.ErrorMessage;


// To load/insert the data on the file 'insert.sql'  
// To use the configurations on application-test.properties
// To start the test context with a random port
@Sql(scripts = "/insert.sql") 
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {
    
    // To inject the component of testRestTemplate
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
     

    // To test the 'login' endpoint when the credentials are correct.
    @Test
    void loginSuccessTest() {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "rayas");
        userInsert.put("password", "rayas123.");

        // When
        ResponseEntity<LoginResponseDto> response = client.postForEntity("/api/users/login", userInsert, LoginResponseDto.class);
        LoginResponseDto loginResponse = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hola rayas has iniciado sesion con exito!", loginResponse.getMessage());
        assertFalse(loginResponse.getToken().isEmpty());
        assertEquals("rayas", loginResponse.getUsername());
    }

    // To test the 'login' endpoint when the credentials are not correct.
    @Test
    void loginUnsuccessTest() {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "Unknown");
        userInsert.put("password", "Unknown123.");

        // When
        ResponseEntity<LoginErrorResponseDto> response = client.postForEntity("/api/users/login", userInsert, LoginErrorResponseDto.class);
        LoginErrorResponseDto body = response.getBody();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Error en la autenticacion username o password incorrectos!", body.getMessage());
        assertEquals("Bad credentials", body.getError());
    }

    // To test the endpoint getUser when we use an existing id and the user is the owner of resouce
    @Test
    void getUserExistingIdOwnerIntegrationTest() {

        // Given
        User userToSearch = UserData.createUser005();
        Long idToSearch = 14L;
        userToSearch.setId(14L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToSearch);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<UserDto> response = client.exchange(
                "/api/users/" + idToSearch,
                HttpMethod.GET,
                entity,
                UserDto.class
        );
        UserDto userResponse = response.getBody();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(userResponse);
        assertEquals(userToSearch.getId(), userResponse.getId());
        assertEquals(userToSearch.getUsername(), userResponse.getUsername());
    }


    // To test the endpoint getUser when we use an existing id and the user is the owner of resouce
    @Test
    void getUserExistingIdNoOwnerIntegrationTest() {

        // Given
        User userToSearch = UserData.createUser005();
        Long idToSearch = 15L;
        userToSearch.setId(14L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToSearch);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/users/" + idToSearch,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the endpoint getUser when we use an inexisting id
    // If the user doesn't exist, then the user doesn't have a token JWT
    @Test
    void getUserInexistingIdIntegrationTest() {
    
        // Given
        Long idToSearch = 99999L;

        // When
        ResponseEntity<?> response  = client.getForEntity("/api/users/" + idToSearch, Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // When a user is inserted, the database always uses the user ID number 1, 
    // so it was necessary to change all the user IDs in the insert.sql file.
    @Test
    void registerUserIntegrationTest() {
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", " nuevo ");
        userInsert.put("password", "nuevo123.");

        // When
        ResponseEntity<UserDto> response = client.postForEntity("/api/users/register", userInsert, UserDto.class);
        UserDto userResponse = response.getBody();

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("nuevo", userResponse.getUsername());
    }

    // To test the endpoint update when we use an existing id and the user is the owner of resource
    @Test
    void putUpdateExistingIdOwnerIntegrationTest()  {

        // Given
        User userToUpdate = UserData.createUser005();
        Long idToUpdate = 14L;
        userToUpdate.setId(14L); // Change the user id for the user id in the insert.sql file

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", " newName  ");
        newUser.put("password", "newName123.");
        
        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the entity with request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newUser, headers);

        ResponseEntity<UserDto> response = client.exchange("/api/users/" + idToUpdate, HttpMethod.PUT, entity, UserDto.class);

        // Then
        UserDto userUpdate = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(14L, userUpdate.getId());
        assertEquals("newName", userUpdate.getUsername());
    }

    // To test the endpoint update when we use an existing id and the user is not the owner of resource
    @Test
    void putUpdateExistingIdNoOwnerIntegrationTest()  {

        // Given
        User userToUpdate = UserData.createUser005();
        Long idToUpdate = 15L;
        userToUpdate.setId(14L); // Change the user id for the user id in the insert.sql file

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", "newName");
        newUser.put("password", "newName123.");
        
        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the entity with request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newUser, headers);

        ResponseEntity<?> response = client.exchange("/api/users/" + idToUpdate, HttpMethod.PUT, entity, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the endpoint update when we use an inexisting id 
    @Test
    void putUpdateInexistingIdIntegrationTest()  {

        // Given
        User userToUpdate = UserData.createUser005();
        Long idToUpdate = 999999L;
        userToUpdate.setId(14L); // Change the user id for the user id in the insert.sql file

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", "newName");
        newUser.put("password", "newName123.");
        
        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the entity with request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newUser, headers);

        ResponseEntity<?> response = client.exchange("/api/users/" + idToUpdate, HttpMethod.PUT, entity, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the endpoint delete when we use an existing id and the user is the owner of resource
    @Test
    void deleteExistingIdOwnerIntegrationTest() {

        // Given
        User userToDelete = UserData.createUser005();
        Long idToDelete = 14L;
        userToDelete.setId(14L); 

        // When
        String token = jwtTokenUtil.createToken(userToDelete);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response = client.exchange(
            "/api/users/" + idToDelete,
            HttpMethod.DELETE,
            entity,
            UserDto.class
        );

        // Then
        UserDto userDeleted = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(idToDelete, userDeleted.getId());
        assertEquals(userToDelete.getUsername(), userDeleted.getUsername());
    }

    // To test the endpoint delete when we use an existing id and the user is not the owner of resource
    @Test
    void deleteExistingIdNoOwnerIntegrationTest() {

        // Given
        User notOwnerUser = UserData.createUser005();
        notOwnerUser.setId(14L); 
        Long idToDelete = 15L; 

        // When
        String token = jwtTokenUtil.createToken(notOwnerUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = client.exchange(
            "/api/users/" + idToDelete,
            HttpMethod.DELETE,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the endpoint delete when we use an inexisting id 
    @Test
    void deleteInexistingIdIntegrationTest() {

        // Given
        Long idToDelete = 999999L; // un id que claramente no existe

        // When
        ResponseEntity<Void> response = client.exchange(
            "/api/users/" + idToDelete,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the validation in the DB: it doesn't allow to insert the same record
    @Test
    void clientPostDuplicateRecordsIntegrationTest() {
    
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "alejandro");
        userInsert.put("password", "alejandro123.");

        // When
        ResponseEntity<ErrorMessage> response = client.postForEntity("/api/users/register", userInsert, ErrorMessage.class);
        ErrorMessage newError = response.getBody();

        // Then
        assertEquals(HttpStatus.CONFLICT.value(), newError.getStatus());
        assertEquals("Error! El usuario que se desea registrar ya se encuentra en la base de datos.", newError.getError());
        assertTrue(newError.getMessage().contains("insert"));
        assertTrue(newError.getMessage().contains("PUBLIC.TBL_USER"));

        LocalDateTime now = LocalDateTime.now();
        assertTrue( Duration.between(newError.getDateTime(), now).toMinutes() < 2 );
    }

    // To test the validation in the DB: it doesn't allow to update a record with the same information as another one
    @Test
    void clientPutDuplicateRecordsIntegrationTest() {
    
        // Given
        User userToUpdate = UserData.createUser005();
        Long idToUpdate = 14L;
        userToUpdate.setId(14L); // Change the user id for the user id in the insert.sql file

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", " alejandro  ");
        newUser.put("password", "alejandro123.");
        
        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the entity with request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newUser, headers);
        ResponseEntity<ErrorMessage> response = client.exchange("/api/users/" + idToUpdate, HttpMethod.PUT, entity, ErrorMessage.class);
        ErrorMessage newError = response.getBody();

        // Then
        assertEquals("Error! Este nombre de usuario al cual se desea actualizar ya lo posee otro usuario.", newError.getError());
        assertEquals(HttpStatus.CONFLICT.value(), newError.getStatus());
        assertTrue(newError.getMessage().contains("update"));
        assertTrue(newError.getMessage().contains("PUBLIC.TBL_USER"));

        LocalDateTime now = LocalDateTime.now();
        assertTrue( Duration.between(newError.getDateTime(), now).toMinutes() < 2 );
    }

}
