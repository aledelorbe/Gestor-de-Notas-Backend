package com.alejandro.gestordenotas.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.Test;

import com.alejandro.gestordenotas.TestConfig;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(UserController.class)
@Import(TestConfig.class)
public class UserControllerTest {
    
    // To inject the dependency that allows for mocking HTTP requests
    @Autowired
    private MockMvc mockMvc;
 
    // To inject the dependency that represents the service to mock
    @MockitoBean
    private UserService service; 

    @Autowired
    private ObjectMapper objectMapper;


    // To test the 'GetfindById' endpoint with an existing user ID
    @Test
    void getfindByIdExistingIdTest() throws Exception {

        // Given
        Long idToSearch = 5L;
        when(service.findById(anyLong())).thenReturn(Optional.of(UserData.createUser002()));

        // When
        MvcResult result = mockMvc.perform(get("/api/users/" + idToSearch))

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.username").value("rayas"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto user = objectMapper.readValue(jsonString, UserDto.class);

        assertNotNull(user);
        assertEquals(5L, user.getId());
        assertEquals("rayas", user.getUsername());

        verify(service).findById(argThat(new CustomCondition(UserData.idsValid, true)));
    }
    
    // To test the endpoint GetfindById with an inexisting id
    @Test
    void getfindByIdInexistingIdTest() throws Exception {
        
        // Given
        Long idToSearch = 999999L;
        when(service.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        mockMvc.perform(get("/api/users/" + idToSearch))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).findById(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the endpoint save
    @Test
    void postSaveTest() throws Exception {

        // Given
        User userInsert = new User(null, "ben", "ben123");
        when(service.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // when
        MvcResult result = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userInsert)))

        // then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("ben"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto newUser = objectMapper.readValue(jsonString, UserDto.class);

        assertEquals("ben", newUser.getUsername());

        verify(service).save(any(User.class));
    }

    // To test the endpoint update when we use an existing id 
    @Test
    void putUpdateExistingIdTest() throws Exception {
    
        // Given
        Long idToUpdate = 2L;
        User userToUpdate = new User(null, "wen", "wen123");
        when(service.update(anyLong(), any(User.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(1)));

        // When
        MvcResult result = mockMvc.perform(put("/api/users/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userToUpdate)))

        // then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.username").value("wen"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto newUser = objectMapper.readValue(jsonString, UserDto.class);

        assertEquals(2L, newUser.getId());
        assertEquals("wen", newUser.getUsername());

        verify(service).update(argThat(new CustomCondition(UserData.idsValid, true)), any(User.class));
    }

    // To test the endpoint update when we use an inexisting id 
    @Test
    void putUpdateInexistingIdTest() throws Exception {
    
        // Given
        Long idToUpdate = 8L;
        User userToUpdate = new User(null, "wen", "wen123");
        when(service.update(anyLong(), any(User.class))).thenReturn(Optional.empty());

        // When
        mockMvc.perform(put("/api/users/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userToUpdate)))

        // then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).update(argThat(new CustomCondition(UserData.idsValid, false)), any(User.class));
    }

    // To test the endpoint delete when we use an existing id 
    @Test
    void deleteExistingIdTest() throws Exception {
    
        // Given
        Long idToDelete = 1L;
        when(service.deleteById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        MvcResult result = mockMvc.perform(delete("/api/users/" + idToDelete))

        // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("alejandro"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        User newUser = objectMapper.readValue(jsonString, User.class);

        assertEquals(1L, newUser.getId());
        assertEquals("alejandro", newUser.getUsername());

        verify(service).deleteById(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the endpoint delete when we use an inexisting id 
    @Test
    void deleteInexistingIdTest() throws Exception {
    
        // Given
        Long idToDelete = 99999L;
        when(service.deleteById(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(delete("/api/users/" + idToDelete))

        // then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
            ;

        verify(service).deleteById(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the method validation
    @Test
    void validationTest() throws Exception {

        // Given
        User userInsert = new User(null, "", "");
        
        // when
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userInsert)))
        
        // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("El campo username must not be blank"))
            .andExpect(jsonPath("$.password").value("El campo password must not be blank"))
        ;

        verify(service, never()).save(any(User.class));
    }

}
