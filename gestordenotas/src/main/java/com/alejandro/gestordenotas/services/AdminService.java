package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.dto.AdminDto;
import com.alejandro.gestordenotas.entities.User;

public interface AdminService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for the admin role
    // -----------------------------
 
    List<AdminDto> getAllUsersWithUserRole();

    Optional<AdminDto> getUserWithUserRole(Long id);

    Optional<User> disableEnableUser(Long userId);

    // Methods aux
    
    List<Long> getAllIdsWithAdminAndSuperAdminRole();

}
