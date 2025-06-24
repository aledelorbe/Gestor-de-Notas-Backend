package com.alejandro.gestordenotas.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.alejandro.gestordenotas.TestConfig;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.data.NoteData;
import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.NoteService;
import com.alejandro.gestordenotas.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(NoteController.class)
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class NoteControllerTest {
    
    // To inject the dependency that allows for mocking HTTP requests
    @Autowired
    private MockMvc mockMvc;
 
    // To inject the dependency that represents the service to mock
    @MockitoBean
    private NoteService service; 

    // To inject the dependency that represents the service to mock
    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    // To test the 'getNotesByUser' endpoint with an existing idUser
    @Test
    void getNotesByUserExistingIdTest() throws Exception {

        // Given
        Long idUserToSearch = 2L;
        String username = "rayas";
        Principal principal = () -> username;
        when(userService.findById(anyLong())).thenReturn(Optional.of(UserData.createUser002()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);

        // When
        MvcResult result = mockMvc.perform(get("/api/users/" + idUserToSearch + "/notes").principal(principal)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(2L))
            .andExpect(jsonPath("$[0].content").value("This is the note No. 2"))
            .andReturn()
        ;

        // Convert the response to an array of objects
        String jsonString = result.getResponse().getContentAsString();
        List<Note> notes = Arrays.asList( objectMapper.readValue(jsonString, Note[].class));

        assertNotNull(notes);
        assertEquals(3, notes.size());
        assertEquals(2L, notes.get(0).getId());
        assertEquals("This is the note No. 2", notes.get(0).getContent());

        verify(userService).findById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'getNotesByUser' endpoint with an inexisting idUser
    @Test
    void getNotesByUserInexistingIdTest() throws Exception {
        
        // Given
        Long idUserToSearch = 999999L;
        String username = "rayas";
        Principal principal = () -> username;
        when(userService.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        
        // When
        mockMvc.perform(get("/api/users/" + idUserToSearch + "/notes").principal(principal)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(userService).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'getNotesByUser' endpoint when the user is not the owner
    @Test
    void getNotesByUserNoOwnerTest() throws Exception {
        
        // Given
        Long idUserToSearch = 999999L;
        String username = "rayas";
        Principal principal = () -> username;
        when(userService.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        
        // When
        mockMvc.perform(get("/api/users/" + idUserToSearch + "/notes").principal(principal)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(userService, never()).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'saveNewNoteByUserId' endpoint when the note was saved
    @Test
    void postSaveNewNoteByUserIdExistingIdTest() throws Exception {

        // Given
        Long idUserToSearch = 5L;
        Note noteToInsert = new Note(null, "This is a new note");
        String username = "rayas";
        Principal principal = () -> username;
        when(service.saveNoteByUser(anyLong(), any(Note.class))).thenReturn(Optional.of(UserData.createUser005()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        
        // When
        MvcResult result = mockMvc.perform(post("/api/users/" + idUserToSearch + "/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToInsert))
            .principal(principal))

        // Then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.username").value("rayas"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        User user = objectMapper.readValue(jsonString, User.class);

        assertNotNull(user);
        assertEquals(5L, user.getId());
        assertEquals("rayas", user.getUsername());

        verify(service).saveNoteByUser(argThat(new CustomCondition(UserData.idsValid, true)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'saveNewNoteByUserId' endpoint when the note was not saved because the 'saveNoteByUser' method returned an
    // empty optional
    @Test
    void postSaveNewNoteByUserIdInexistingIdTest() throws Exception {

        // Given
        Long idUserToSearch = 99999L;
        Note noteToInsert = new Note(null, "This is a new note");
        String username = "rayas";
        Principal principal = () -> username;
        when(service.saveNoteByUser(anyLong(), any(Note.class))).thenReturn(Optional.empty());
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        
        // When
        mockMvc.perform(post("/api/users/" + idUserToSearch + "/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToInsert))
            .principal(principal))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).saveNoteByUser(argThat(new CustomCondition(UserData.idsValid, false)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'saveNewNoteByUserId' endpoint when the note was not saved because the user is not the owner
    @Test
    void postSaveNewNoteByUserIdNoOwnerTest() throws Exception {

        // Given
        Long idUserToSearch = 5L;
        Note noteToInsert = new Note(null, "This is a new note");
        String username = "rayas";
        Principal principal = () -> username;
        when(service.saveNoteByUser(anyLong(), any(Note.class))).thenReturn(Optional.of(UserData.createUser005()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        
        // When
        mockMvc.perform(post("/api/users/" + idUserToSearch + "/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToInsert))
            .principal(principal))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service, never()).saveNoteByUser(argThat(new CustomCondition(UserData.idsValid, true)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'editNoteByUserId' endpoint when the note can be updated
    @Test
    void patchEditNoteByUserIdSuccessUpdateTest() throws Exception {

        // Given
        Long idUserToSearch = 3L;
        Long idNoteToSearch = 3L;
        Note noteToUpdate = new Note(null, "This is a note to update");
        String username = "celia";
        Principal principal = () -> username;
        when(service.editNoteByUser(anyLong(), anyLong(), any(Note.class))).thenReturn(Optional.of(UserData.createUser003()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);

        // When
        MvcResult result = mockMvc.perform(patch("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToUpdate))
            .principal(principal))
        
        // Then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(3L))
            .andExpect(jsonPath("$.username").value("celia"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        User user = objectMapper.readValue(jsonString, User.class);

        assertNotNull(user);
        assertEquals(3L, user.getId());
        assertEquals("celia", user.getUsername());

        verify(service).editNoteByUser(argThat(new CustomCondition(UserData.idsValid, true)), argThat(new CustomCondition(NoteData.idsValid, true)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'editNoteByUserId' endpoint when the note can not be updated because the 'editNoteByUser' method returned an 
    // empty optional
    @Test
    void patchEditNoteByUserIdUnsuccessUpdateTest() throws Exception {

        // Given
        Long idUserToSearch = 99999L;
        Long idNoteToSearch = 999999L;
        Note noteToUpdate = new Note(null, "This is a note to update");
        String username = "celia";
        Principal principal = () -> username;
        when(service.editNoteByUser(anyLong(), anyLong(), any(Note.class))).thenReturn(Optional.empty());
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        
        // When
        mockMvc.perform(patch("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToUpdate))
            .principal(principal))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).editNoteByUser(argThat(new CustomCondition(UserData.idsValid, false)), argThat(new CustomCondition(NoteData.idsValid, false)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'editNoteByUserId' endpoint when the note can not be updated because the user is not owner
    @Test
    void patchEditNoteByUserIdNoOwnerTest() throws Exception {

        // Given
        Long idUserToSearch = 999999L;
        Long idNoteToSearch = 3L;
        Note noteToUpdate = new Note(null, "This is a note to update");
        String username = "celia";
        Principal principal = () -> username;
        when(service.editNoteByUser(anyLong(), anyLong(), any(Note.class))).thenReturn(Optional.of(UserData.createUser003()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        
        // When
        mockMvc.perform(patch("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToUpdate))
            .principal(principal))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service, never()).editNoteByUser(argThat(new CustomCondition(UserData.idsValid, false)), argThat(new CustomCondition(NoteData.idsValid, false)), any(Note.class));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'deleteNoteByUser' endpoint when the note can be deleted
    @Test
    void deleteNoteByUserIdSuccessTest() throws Exception {

        // Given
        Long idUserToSearch = 3L;
        Long idNoteToSearch = 3L;
        String username = "celia";
        Principal principal = () -> username;
        when(service.deleteNoteByUser(anyLong(), anyLong())).thenReturn(Optional.of(UserData.createUser003()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);

        // When
        MvcResult result = mockMvc.perform(delete("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
                .principal(principal))

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(3L))
            .andExpect(jsonPath("$.username").value("celia"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto user = objectMapper.readValue(jsonString, UserDto.class);

        assertNotNull(user);
        assertEquals(3L, user.getId());
        assertEquals("celia", user.getUsername());

        verify(service).deleteNoteByUser(argThat(new CustomCondition(UserData.idsValid, true)),
                                        argThat(new CustomCondition(NoteData.idsValid, true)));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'deleteNoteByUser' endpoint when the note can not be deleted because the 'deleteNoteByUser' method returned 
    // an empty optional
    @Test
    void deleteNoteByUserIdNoteNotFoundTest() throws Exception {

        // Given
        Long idUserToSearch = 99999L;
        Long idNoteToSearch = 999999L;
        String username = "celia";
        Principal principal = () -> username;
        when(service.deleteNoteByUser(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);

        // When
        mockMvc.perform(delete("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
                .principal(principal))

            // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));
        
        verify(service).deleteNoteByUser(argThat(new CustomCondition(UserData.idsValid, false)),
                                        argThat(new CustomCondition(NoteData.idsValid, false)));
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'deleteNoteByUser' endpoint when the note can not be deleted because the user is not owner
    @Test
    void deleteNoteByUserIdNoOwnerTest() throws Exception {

        // Given
        Long idUserToSearch = 999999L;
        Long idNoteToSearch = 3L;
        String username = "celia";
        Principal principal = () -> username;
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(false);

        // When
        mockMvc.perform(delete("/api/users/" + idUserToSearch + "/notes/" + idNoteToSearch)
                .principal(principal))

            // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));

        // No se debe invocar el método deleteNoteByUser
        verify(service, never()).deleteNoteByUser(anyLong(), anyLong());
        verify(userService).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'validation' method
    @Test
    void validationTest() throws Exception {

        // Given
        Long idUserToSearch = 5L;
        Note noteToInsert = new Note(null, "");
        String username = "rayas";
        Principal principal = () -> username;
        when(service.saveNoteByUser(anyLong(), any(Note.class))).thenReturn(Optional.of(UserData.createUser005()));
        when(userService.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        
        // When
        mockMvc.perform(post("/api/users/" + idUserToSearch + "/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noteToInsert))
            .principal(principal))
        
        // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.content").value("El campo content must not be blank"))
        ;

        verify(userService, never()).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
        verify(service, never()).saveNoteByUser(argThat(new CustomCondition(UserData.idsValid, true)), any(Note.class));
    }

}