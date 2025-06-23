package com.alejandro.gestordenotas.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    
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
        String username = "rayas";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.findById(anyLong())).thenReturn(Optional.of(UserData.createUser005()));

        // When
        MvcResult result = mockMvc.perform(get("/api/users/" + idToSearch).principal(principal)) 

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
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }
    
    // To test the 'GetfindById' endpoint with an inexisting id
    @Test
    void getfindByIdInexistingIdTest() throws Exception {
        
        // Given
        Long idToSearch = 999999L;
        String username = "desconocido";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        mockMvc.perform(get("/api/users/" + idToSearch).principal(principal)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'GetfindById' endpoint when the user is not the owner
    @Test
    void getfindByIdNoOwnerTest() throws Exception {
        
        // Given
        Long idToSearch = 999999L;
        String username = "rayas";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        when(service.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        mockMvc.perform(get("/api/users/" + idToSearch).principal(principal)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service, never()).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'save' endpoint 
    @Test
    void postSaveTest() throws Exception {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "ben");
        userInsert.put("password", "ben123");
        when(service.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MvcResult result = mockMvc.perform(post("/api/users/register")
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

    // To test the 'update' endpoint when we use an existing id 
    @Test
    void putUpdateExistingIdTest() throws Exception {
    
        // Given
        Long idToUpdate = 2L;
        String username = "wen";
        Principal principal = () -> username;
        Map<String, Object> userToUpdate = new HashMap<>();
        userToUpdate.put("username", username);
        userToUpdate.put("password", "wen123");
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.update(anyLong(), any(User.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(1)));

        // When
        MvcResult result = mockMvc.perform(put("/api/users/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userToUpdate))
            .principal(principal))

        // then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("wen"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto newUser = objectMapper.readValue(jsonString, UserDto.class);

        assertEquals("wen", newUser.getUsername());

        verify(service).update(argThat(new CustomCondition(UserData.idsValid, true)), any(User.class));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'update' endpoint when we use an inexisting id 
    @Test
    void putUpdateInexistingIdTest() throws Exception {
    
        // Given
        Long idToUpdate = 8L;
        String username = "desconocido";
        Principal principal = () -> username;
        Map<String, Object> userToUpdate = new HashMap<>();
        userToUpdate.put("username", username);
        userToUpdate.put("password", "wen123");
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.update(anyLong(), any(User.class))).thenReturn(Optional.empty());

        // When
        mockMvc.perform(put("/api/users/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userToUpdate))
            .principal(principal))

        // then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).update(argThat(new CustomCondition(UserData.idsValid, false)), any(User.class));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'update' endpoint when the user is not the owner
    @Test
    void putUpdateNoOwnerTest() throws Exception {
    
        // Given
        Long idToUpdate = 8L;
        String username = "wen";
        Principal principal = () -> username;
        Map<String, Object> userToUpdate = new HashMap<>();
        userToUpdate.put("username", username);
        userToUpdate.put("password", "wen123");
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        when(service.update(anyLong(), any(User.class))).thenReturn(Optional.empty());
        
        // When
        mockMvc.perform(put("/api/users/" + idToUpdate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userToUpdate))
            .principal(principal))
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service, never()).findById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'delete' endpoint when we use an existing id 
    @Test
    void deleteExistingIdTest() throws Exception {
    
        // Given
        Long idToDelete = 1L;
        String username = "alejandro";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.deleteById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        MvcResult result = mockMvc.perform(delete("/api/users/" + idToDelete).principal(principal))

        // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("alejandro"))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        UserDto newUser = objectMapper.readValue(jsonString, UserDto.class);

        assertEquals(1L, newUser.getId());
        assertEquals("alejandro", newUser.getUsername());

        verify(service).deleteById(argThat(new CustomCondition(UserData.idsValid, true)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'delete' endpoint when we use an inexisting id 
    @Test
    void deleteInexistingIdTest() throws Exception {
    
        // Given
        Long idToDelete = 99999L;
        String username = "desconocido";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(true);
        when(service.deleteById(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(delete("/api/users/" + idToDelete).principal(principal))

        // then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).deleteById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, false)), any(Principal.class));
    }

    // To test the 'delete' endpoint when the user is not owner
    @Test
    void deleteNoOwnerTest() throws Exception {
    
        // Given
        Long idToDelete = 1L;
        String username = "alejandro";
        Principal principal = () -> username;
        when(service.isOwner(anyLong(), any(Principal.class))).thenReturn(false);
        when(service.deleteById(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        mockMvc.perform(delete("/api/users/" + idToDelete).principal(principal))

        // then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service, never()).deleteById(argThat(new CustomCondition(UserData.idsValid, false)));
        verify(service).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

    // To test the 'validation' method
    @Test
    void validationTest() throws Exception {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "");
        userInsert.put("password", "");
        when(service.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userInsert)))
        
        // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("El campo username must not be blank"))
            .andExpect(jsonPath("$.password").value("El campo password must not be blank"))
        ;

        verify(service, never()).save(any(User.class));
        verify(service, never()).isOwner(argThat(new CustomCondition(UserData.idsValid, true)), any(Principal.class));
    }

}
