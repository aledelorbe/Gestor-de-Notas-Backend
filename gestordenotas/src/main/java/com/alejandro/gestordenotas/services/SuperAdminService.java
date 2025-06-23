package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.dto.SuperAdminDto;
import com.alejandro.gestordenotas.entities.User;


public interface SuperAdminService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for the super admin role
    // -----------------------------
    
    List<SuperAdminDto> getAllUsersWithUserAndAdminRole();

    Optional<SuperAdminDto> getUserWithUserAndAdminRole(Long id);
    
    Optional<User> addRemoveAdminRoleFromUser(Long id);

    // Methods aux

    boolean isSuperAdmin(Long id);

}
