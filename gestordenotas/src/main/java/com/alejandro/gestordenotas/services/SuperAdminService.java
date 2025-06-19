package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;

public interface SuperAdminService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for the super admin role
    // -----------------------------
    
    List<UserDto> getAllUsersWithRoleUserAndAdmin();
    
    Optional<User> addRemoveAdminRoleFromUser(Long id);

    // Methods aux

    boolean isSuperAdmin(Long id);

}
