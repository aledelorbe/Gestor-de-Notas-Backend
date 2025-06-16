package com.alejandro.gestordenotas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

    // ---------------------
    // Custom queries ------
    // ---------------------

    // To get all the notes of certain user
    @Query("""
            SELECT cl.notes
            FROM User cl
            WHERE cl.id = ?1
            """)
    List<Note> getNotesByUserId(Long id_user);

    // To get a user based on its name
    Optional<User> findByUsername(String username);

    // To get all of the users with role 'users'
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.services.dto.UserDto(u.id, u.username, u.enabled)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 2 OR r.id = 3
        )
    """)
    List<UserDto> getAllUsersWithRoleUser();
    
    // To get a specific user who only have the role called 'user' 
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.services.dto.UserDto(u.id, u.username, u.enabled)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 2 OR r.id = 3
        ) and u.id = ?1
    """)
    Optional<UserDto> getUserWithRoleUser(Long id);
    
    // To get all of the users with role 'users' and role 'admin'
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.services.dto.AdminDto(u.id, u.username, u.enabled, u.admin)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 3
        )
    """)
    List<UserDto> getAllUsersWithRoleUserAndAdmin();

    // To get all of the id's of users with role 'admin' and 'super admin'
    @Query("""
        SELECT u.id
        FROM User u
        WHERE u.id IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 2 OR r.id = 3
        )
    """)
    List<Long> getAllIdWithRoleAdminAndSuperAdmin();

    // To get the id of the user with the super admin role
    @Query("""
        SELECT u.id
        FROM User u
        WHERE u.id IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 3
        )
    """)
    Long getIdOfSuperAdmin();

}