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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.alejandro.gestordenotas.TestConfig;
import com.alejandro.gestordenotas.data.CustomCondition;
import com.alejandro.gestordenotas.data.UserData;
import com.alejandro.gestordenotas.dto.AdminDto;
import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.AdminService;
import com.alejandro.gestordenotas.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(AdminController.class)
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {
    
    // To inject the dependency that allows for mocking HTTP requests
    @Autowired
    private MockMvc mockMvc;
 
    // To inject the dependency that represents the service to mock
    @MockitoBean
    private UserService userService; 

    // To inject the dependency that represents the service to mock
    @MockitoBean
    private AdminService service; 

    @Autowired
    private ObjectMapper objectMapper;

    
    // To test the 'getUserWithUserRole' endpoint with an existing user ID
    @Test
    void getUserWithUserRoleExistingIdTest() throws Exception {

        // Given
        Long idToSearch = 5L;
        AdminDto adminDto = new AdminDto(UserData.createUser005().getId(), UserData.createUser005().getUsername(), true);
        String username = "rayas";
        Principal principal = () -> username;
        when(service.getUserWithUserRole(anyLong())).thenReturn(Optional.of(adminDto));

        // When
        MvcResult result = mockMvc.perform(get("/api/admins/user/" + idToSearch).principal(principal)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.username").value("rayas"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        AdminDto user = objectMapper.readValue(jsonString, AdminDto.class);

        assertNotNull(user);
        assertEquals(5L, user.getId());
        assertEquals("rayas", user.getUsername());
        assertTrue(user.isEnabled());
        
        verify(service).getUserWithUserRole(argThat(new CustomCondition(UserData.idsValid, true)));
    }
    
    // To test the 'getUserWithUserRole' endpoint with an inexisting id
    @Test
    void getUserWithUserRoleInexistingIdTest() throws Exception {
        
        // Given
        Long idToSearch = 9999999L;
        String username = "desconocido";
        Principal principal = () -> username;
        when(service.getUserWithUserRole(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(get("/api/admins/user/" + idToSearch).principal(principal)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).getUserWithUserRole(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'saveNewUserAdmin' endpoint 
    @Test
    void postSaveTest() throws Exception {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "ben");
        userInsert.put("password", "ben123");
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MvcResult result = mockMvc.perform(post("/api/admins")
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

        verify(userService).save(any(User.class));
    }

    // To test the 'validation' method
    @Test
    void validationTest() throws Exception {

        // Given
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("username", "");
        userInsert.put("password", "");
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        mockMvc.perform(post("/api/admins")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userInsert)))
        
        // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("El campo username must not be blank"))
            .andExpect(jsonPath("$.password").value("El campo password must not be blank"))
        ;

        verify(userService, never()).save(any(User.class));
    }
    
    // To test the 'disableEnableUser' method when the user was disabled or enabled
    @Test
    void disableEnableUserSuccessTest() throws Exception {

        // Given
        Long idToSearch = 6L;
        when(service.getAllIdsWithAdminAndSuperAdminRole()).thenReturn(UserData.userIdsWithAdminOrSuperAdminRole);
        when(service.disableEnableUser(anyLong())).thenReturn(Optional.of(UserData.createUser006()));

        // When
        MvcResult result = mockMvc.perform(patch("/api/admins/user/" + idToSearch)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(6L))
            .andExpect(jsonPath("$.username").value("pancha"))
            .andExpect(jsonPath("$.enabled").value(false))
            .andExpect(jsonPath("$", Matchers.aMapWithSize(3)))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        AdminDto user = objectMapper.readValue(jsonString, AdminDto.class);

        assertNotNull(user);
        assertEquals(6L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        
        verify(service).getAllIdsWithAdminAndSuperAdminRole();
        verify(service).disableEnableUser(anyLong());
    }

    // To test the 'disableEnableUser' method when the user was not disabled or enabled because the 'disableEnableUser' method 
    // returned an empty optional
    @Test
    void disableEnableUserUnsuccessTest() throws Exception {

        // Given
        Long idToSearch = 6L;
        when(service.getAllIdsWithAdminAndSuperAdminRole()).thenReturn(UserData.userIdsWithAdminOrSuperAdminRole);
        when(service.disableEnableUser(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(patch("/api/admins/user/" + idToSearch)) 

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).getAllIdsWithAdminAndSuperAdminRole();
        verify(service).disableEnableUser(anyLong());
    }

    // To test the 'disableEnableUser' method when the user was not disabled or enabled because the user id belongs to an user with
    // the admin or super admin role
    @Test
    void disableEnableUserUnsuccessIdsTest() throws Exception {

        // Given
        Long idToSearch = 1L;
        when(service.getAllIdsWithAdminAndSuperAdminRole()).thenReturn(UserData.userIdsWithAdminOrSuperAdminRole);
        when(service.disableEnableUser(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(patch("/api/admins/user/" + idToSearch)) 

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).getAllIdsWithAdminAndSuperAdminRole();
        verify(service, never()).disableEnableUser(anyLong());
    }

}
