package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;

public interface UserService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // Methods for user role
    List<User> findAll();
    
    Optional<User> findById(Long id);
    
    User save(User user);
    
    Optional<User> update(Long id, User user);
    
    Optional<User> deleteById(Long id);
    
    // Methods for super admin role
    List<UserDto> getAllUsersWithRoleUserAndAdmin();
    
    User convertUserIntoAdmin(User userDb);

    // Methods aux
    List<Long> getAllIdWithRoleAdminAndSuperAdmin();

    boolean isSuperAdmin(Long id);

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    List<Note> getNotesByUserId(Long id_user);

    Optional<User> getByUsername(String username);

}