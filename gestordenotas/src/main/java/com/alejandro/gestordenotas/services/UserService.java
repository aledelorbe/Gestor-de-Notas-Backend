package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.dto.UserDto;

public interface UserService {

    // Declaration of methods to use in 'serviceImp' file

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // Methods for user role
    public List<User> findAll();
    
    public Optional<User> findById(Long id);
    
    public User save(User user);
    
    public Optional<User> update(Long id, User user);
    
    public Optional<User> deleteById(Long id);
    
    // Methods for admin role
    public List<UserDto> getAllUsersWithRoleUser();

    public Optional<UserDto> getUserWithRoleUser(Long id);

    public User enabledUser(User userDb);
    
    // Methods for super admin role
    public List<UserDto> getAllUsersWithRoleUserAndAdmin();
    
    public User convertUserIntoAdmin(User userDb);

    // Methods aux
    public List<Long> getAllIdWithRoleAdminAndSuperAdmin();

    public boolean isSuperAdmin(Long id);

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    public List<Note> getNotesByUserId(Long id_user);

    public Optional<User> getByUsername(String username);

}