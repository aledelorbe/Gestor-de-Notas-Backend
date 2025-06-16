package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.User;

public interface AdminService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for the admin role
    // -----------------------------
 
    List<UserDto> getAllUsersWithRoleUser();

    Optional<UserDto> getUserWithRoleUser(Long id);

    User enabledUser(User userDb);

}
