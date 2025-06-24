package com.alejandro.gestordenotas.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

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
import com.alejandro.gestordenotas.dto.SuperAdminDto;
import com.alejandro.gestordenotas.services.AdminService;
import com.alejandro.gestordenotas.services.SuperAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(SuperAdminController.class)
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class SuperAdminControllerTest {

    // To inject the dependency that allows for mocking HTTP requests
    @Autowired
    private MockMvc mockMvc;
 
    // To inject the dependency that represents the service to mock
    @MockitoBean
    private AdminService adminService; 

    // To inject the dependency that represents the service to mock
    @MockitoBean
    private SuperAdminService service; 

    @Autowired
    private ObjectMapper objectMapper;
    


    // To test the 'getUserWithUserAndAdminRole' endpoint with an existing user ID
    @Test
    void getUserWithUserAndAdminRoleExistingIdTest() throws Exception {

        // Given
        Long idToSearch = 5L;
        SuperAdminDto superAdminDto = new SuperAdminDto(UserData.createUser005().getId(), UserData.createUser005().getUsername(), true, UserData.createUser005().isAdmin());
        when(service.getUserWithUserAndAdminRole(anyLong())).thenReturn(Optional.of(superAdminDto));

        // When
        MvcResult result = mockMvc.perform(get("/api/super-admins/user-and-admin/" + idToSearch)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.username").value("rayas"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.admin").value(false))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        SuperAdminDto user = objectMapper.readValue(jsonString, SuperAdminDto.class);

        assertNotNull(user);
        assertEquals(5L, user.getId());
        assertEquals("rayas", user.getUsername());
        assertTrue(user.isEnabled());
        assertFalse(user.isAdmin());
        
        verify(service).getUserWithUserAndAdminRole(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'getUserWithUserAndAdminRole' endpoint with an inexisting id
    @Test
    void getUserWithUserAndAdminRoleInexistingIdTest() throws Exception {
        
        // Given
        Long idToSearch = 9999999L;
        when(service.getUserWithUserAndAdminRole(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(get("/api/super-admins/user-and-admin/" + idToSearch)) 
        
        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;

        verify(service).getUserWithUserAndAdminRole(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when the admin role was removed from or added to the user
    @Test
    void addRemoveAdminRoleFromUserSuccessTest() throws Exception {

        // Given
        Long idToSearch = 6L;
        when(service.isSuperAdmin(anyLong())).thenReturn(false);
        when(service.addRemoveAdminRoleFromUser(anyLong())).thenReturn(Optional.of(UserData.createUser006()));

        // When
        MvcResult result = mockMvc.perform(patch("/api/super-admins/convert-user-into-admin/" + idToSearch)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(6L))
            .andExpect(jsonPath("$.username").value("pancha"))
            .andExpect(jsonPath("$.enabled").value(false))
            .andExpect(jsonPath("$.admin").value(false))
            .andExpect(jsonPath("$", Matchers.aMapWithSize(4)))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        SuperAdminDto user = objectMapper.readValue(jsonString, SuperAdminDto.class);

        assertNotNull(user);
        assertEquals(6L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        assertFalse(user.isAdmin());
        
        verify(service).isSuperAdmin(anyLong());
        verify(service).addRemoveAdminRoleFromUser(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when the user was not disabled or enabled because the 
    // 'addRemoveAdminRoleFromUser' method returned an empty optional
    @Test
    void addRemoveAdminRoleFromUserUnsuccessTest() throws Exception {

        // Given
        Long idToSearch = 999999L;
        when(service.isSuperAdmin(anyLong())).thenReturn(false);
        when(service.addRemoveAdminRoleFromUser(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(patch("/api/super-admins/convert-user-into-admin/" + idToSearch))

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).isSuperAdmin(anyLong());
        verify(service).addRemoveAdminRoleFromUser(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'addRemoveAdminRoleFromUser' method when the user was not disabled or enabled because the 
    // user id belongs to an user with the super admin role
    @Test
    void addRemoveAdminRoleFromUserUnsuccessIdTest() throws Exception {

        // Given
        Long idToSearch = 1L;
        when(service.isSuperAdmin(anyLong())).thenReturn(true);
        when(service.addRemoveAdminRoleFromUser(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        mockMvc.perform(patch("/api/super-admins/convert-user-into-admin/" + idToSearch))

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).isSuperAdmin(anyLong());
        verify(service, never()).addRemoveAdminRoleFromUser(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'disableEnableUser' method when the user was disabled or enabled
    @Test
    void disableEnableUserSuccessTest() throws Exception {

        // Given
        Long idToSearch = 6L;
        when(service.isSuperAdmin(anyLong())).thenReturn(false);
        when(adminService.disableEnableUser(anyLong())).thenReturn(Optional.of(UserData.createUser006()));

        // When
        MvcResult result = mockMvc.perform(patch("/api/super-admins/disable-enable-user/" + idToSearch)) 

        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.id").value(6L))
            .andExpect(jsonPath("$.username").value("pancha"))
            .andExpect(jsonPath("$.enabled").value(false))
            .andExpect(jsonPath("$.admin").value(false))
            .andExpect(jsonPath("$", Matchers.aMapWithSize(4)))
            .andReturn()
        ;

        // Convert the response to an object
        String jsonString = result.getResponse().getContentAsString();
        SuperAdminDto user = objectMapper.readValue(jsonString, SuperAdminDto.class);

        assertNotNull(user);
        assertEquals(6L, user.getId());
        assertEquals("pancha", user.getUsername());
        assertFalse(user.isEnabled());
        assertFalse(user.isAdmin());
        
        verify(service).isSuperAdmin(anyLong());
        verify(adminService).disableEnableUser(argThat(new CustomCondition(UserData.idsValid, true)));
    }

    // To test the 'disableEnableUser' method when the user was not disabled or enabled because the 'disableEnableUser' method 
    // returned an empty optional
    @Test
    void disableEnableUserUnsuccessTest() throws Exception {

        // Given
        Long idToSearch = 999999L;
        when(service.isSuperAdmin(anyLong())).thenReturn(false);
        when(adminService.disableEnableUser(anyLong())).thenReturn(Optional.empty());

        // When
        mockMvc.perform(patch("/api/super-admins/disable-enable-user/" + idToSearch)) 

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).isSuperAdmin(anyLong());
        verify(adminService).disableEnableUser(argThat(new CustomCondition(UserData.idsValid, false)));
    }

    // To test the 'disableEnableUser' method when the user was not disabled or enabled because the user id belongs to an user with
    // the super admin role
    @Test
    void disableEnableUserUnsuccessIdTest() throws Exception {

        // Given
        Long idToSearch = 1L;
        when(service.isSuperAdmin(anyLong())).thenReturn(true);
        when(adminService.disableEnableUser(anyLong())).thenReturn(Optional.of(UserData.createUser001()));

        // When
        mockMvc.perform(patch("/api/super-admins/disable-enable-user/" + idToSearch)) 

        // Then
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
        ;
        
        verify(service).isSuperAdmin(anyLong());
        verify(adminService, never()).disableEnableUser(argThat(new CustomCondition(UserData.idsValid, true)));
    }

}
