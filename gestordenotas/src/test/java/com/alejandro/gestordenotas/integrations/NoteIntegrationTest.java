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

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.utils.JwtTokenUtil;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.dto.UserDto;


// To load/insert the data on the file 'insert.sql'  
// To use the configurations on application-test.properties
// To start the test context with a random port
@Sql(scripts = "/insert.sql") 
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NoteIntegrationTest {
    
    // To inject the component of testRestTemplate
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    // To test the endpoint getNotesByUser with an existing idUser and the user is the owner of resource
    @Test
    void getNotesByUserExistingIdOwnerIntegrationTest() {

        // Given
        User userToSearch = UserData.createUser002();
        Long idUserToSearch = 11L;
        userToSearch.setId(11L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToSearch);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Note[]> response = client.exchange(
                "/api/users/" + idUserToSearch + "/notes",
                HttpMethod.GET,
                entity,
                Note[].class
        );
        List<Note> notes = Arrays.asList(response.getBody());  

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(notes.isEmpty());
        assertEquals(3, notes.size());
        assertEquals(21, notes.get(0).getId());
        assertEquals("This is the note No. 2", notes.get(0).getContent());
    }

    // To test the endpoint getNotesByUser with an existing idUser and the user is not the owner of resource
    @Test
    void getNotesByUserExistingIdNoOwnerIntegrationTest() {

        // Given
        User userToSearch = UserData.createUser002();
        Long idUserToSearch = 12L;
        userToSearch.setId(11L); // Change the user id for the user id in the insert.sql file

        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToSearch);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); 
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<?> response = client.exchange(
                "/api/users/" + idUserToSearch + "/notes",
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the endpoint getNotesByUser with an inexisting id
    // If the user doesn't exist, then the user doesn't have a token JWT
    @Test
    void getNotesByUserInexistingIdIntegrationTest() {
    
        // Given
        Long idUserToSearch = 999999L;

        // When
        ResponseEntity<?> response  = client.getForEntity("/api/users/" + idUserToSearch + "/notes", Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'saveNewNoteByUserId' endpoint when the idUser exists and the use is the owner of resource
    @Test
    void postSaveNewNoteByUserIdExistingIdOwnerIntegrationTest() {
        // Given
        Long idUserToSearch = 14L;
        Note noteToInsert = new Note(null, "This is a new note");

        // Usuario que será el dueño de la nota
        User userToUpdate = UserData.createUser005();
        userToUpdate.setId(14L);

        // Token JWT para autenticación
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Encabezados HTTP con token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Cuerpo de la noteición
        HttpEntity<Note> entity = new HttpEntity<>(noteToInsert, headers);

        // When
        ResponseEntity<UserDto> response = client.exchange(
            "/api/users/" + idUserToSearch + "/notes",
            HttpMethod.POST,
            entity,
            UserDto.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idUserToSearch, response.getBody().getId());
        assertEquals("rayas", response.getBody().getUsername());
    }

    // To test the 'saveNewNoteByUserId' endpoint when the idUser exists and the use is not the owner of resource
    @Test
    void postSaveNewNoteByUserIdExistingIdNoOwnerIntegrationTest() {
        // Given
        Long idUserToSearch = 15L;
        Note noteToInsert = new Note(null, "This is a new note");

        // Usuario que será el dueño de la nota
        User userToUpdate = UserData.createUser005();
        userToUpdate.setId(14L);

        // Token JWT para autenticación
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Encabezados HTTP con token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Cuerpo de la noteición
        HttpEntity<Note> entity = new HttpEntity<>(noteToInsert, headers);

        // When
        ResponseEntity<?> response = client.exchange(
            "/api/users/" + idUserToSearch + "/notes",
            HttpMethod.POST,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'saveNewNoteByUserId' endpoint when the idUser doesnt exist
    @Test
    void postSaveNewNoteByUserIdInexistingIdIntegrationTest() {
    
        // Given
        Long idUserToSearch = 999999L;
        Note noteToInsert = new Note(null, "This is a new note");

        // When
        ResponseEntity<?> response = client.postForEntity("/api/users/" + idUserToSearch + "/notes", noteToInsert, Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'editNoteByUserId' endpoint when the idUser and idNote exist, the user is the owner of 
    // token and the note was updated (because the note belogs to the user)
    @Test
    void putEditNoteByUserIdPetIdExistingIdOwnerIntegrationTest() {

        // Given
        Long idUserToSearch = 12L;
        User userToUpdate = UserData.createUser003();
        userToUpdate.setId(12L);
        Long idNoteToSearch = 51L;
        Note noteToUpdate = new Note(null, "This is an update note");

        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Note> entity = new HttpEntity<>(noteToUpdate, headers);
        ResponseEntity<UserDto> response = client.exchange("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch, HttpMethod.PATCH, entity, UserDto.class);
        UserDto newUser = response.getBody();

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(12L, newUser.getId());
        assertEquals("celia", newUser.getUsername());
    }

    // To test the 'editNoteByUserId' endpoint when the idUser and idNote exist, the user is the owner of 
    // token and the note was not updated (because the note doesnt belong to the user)
    @Test
    void putEditNoteByUserIdPetIdExistingIdNoOwnerIntegrationTest() {

        // Given
        Long idUserToSearch = 12L;
        User userToUpdate = UserData.createUser003();
        userToUpdate.setId(12L);
        Long idNoteToSearch = 91L;
        Note noteToUpdate = new Note(null, "This is an update note");

        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Note> entity = new HttpEntity<>(noteToUpdate, headers);
        ResponseEntity<?> response = client.exchange("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch, HttpMethod.PATCH, entity, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'editNoteByUserId' endpoint when the idUser exist, the user is the owner of token 
    // but the note id doesnt exist
    @Test
    void putEditNoteByUserIdExistingIdOwnerNoPetIdIntegrationTest() {

        // Given
        Long idUserToSearch = 12L;
        User userToUpdate = UserData.createUser003();
        userToUpdate.setId(12L);
        Long idNoteToSearch = 99999L;
        Note noteToUpdate = new Note(null, "This is an update note");

        // When
        // Generate a valid token for this user
        String token = jwtTokenUtil.createToken(userToUpdate);

        // Prepares the headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Note> entity = new HttpEntity<>(noteToUpdate, headers);
        ResponseEntity<?> response = client.exchange("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch, HttpMethod.PATCH, entity, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // To test the 'editNoteByUserId' endpoint when the idUser
    // This implies that the token is invalid or missing.
    @Test
    void putEditNoteByUserIdInexistingIdIntegrationTest() {

        // Given
        Long idUserToSearch = 99999L;
        Long idNoteToSearch = 99999L;
        Note noteToUpdate = new Note(null, "This is an update note");

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Note> entity = new HttpEntity<>(noteToUpdate, headers);
        ResponseEntity<?> response = client.exchange("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch, HttpMethod.PATCH, entity, Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    // success case: the user id and note id exist, the user has a valid token
    // and the note was deleted (because the note belongs to the user)
    @Test
    void deleteNoteByUserIdExistingUserAndNote_OwnerIntegrationTest() {
        // Given
        Long userId = 12L;
        Long noteId = 51L;
        User user = UserData.createUser003();
        user.setId(userId);

        // When
        String token = jwtTokenUtil.createToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response = client.exchange(
            "/api/users/" + userId + "/notes/" + noteId,
            HttpMethod.DELETE,
            entity,
            UserDto.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        assertEquals("celia", response.getBody().getUsername());
    }

    // unsuccess case: the user id and note id exist, the user has a valid token
    // and the note was not deleted (because the note doesnt belong to the user)
    @Test
    void deleteNoteByUserIdExistingUserAndNote_NotOwnerIntegrationTest() {
        // Given
        Long userId = 12L;
        Long noteId = 91L;
        User user = UserData.createUser003();
        user.setId(userId);

        // When
        String token = jwtTokenUtil.createToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = client.exchange(
            "/api/users/" + userId + "/notes/" + noteId,
            HttpMethod.DELETE,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // unsuccess case: the user id exist but the note id doesnt exist and the user has a valid token
    @Test
    void deleteNoteByUserIdExistingUserButNoteDoesNotExistIntegrationTest() {
        // Given
        Long userId = 12L;
        Long noteId = 99999L;
        User user = UserData.createUser003();
        user.setId(userId);

        // When
        String token = jwtTokenUtil.createToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = client.exchange(
            "/api/users/" + userId + "/notes/" + noteId,
            HttpMethod.DELETE,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // unsuccess case: there isnt a token.
    // Its the same case when there isnt a valid token.
    @Test
    void deleteNoteByUserIdWithoutTokenIntegrationTest() {
        // Given
        Long userId = 12L;
        Long noteId = 51L;

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = client.exchange(
            "/api/users/" + userId + "/notes/" + noteId,
            HttpMethod.DELETE,
            entity,
            Void.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

}