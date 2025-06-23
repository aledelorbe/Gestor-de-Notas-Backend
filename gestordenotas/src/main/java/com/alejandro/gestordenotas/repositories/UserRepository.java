package com.alejandro.gestordenotas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.dto.AdminDto;
import com.alejandro.gestordenotas.dto.SuperAdminDto;
import com.alejandro.gestordenotas.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

    // ---------------------
    // Custom queries ------
    // ---------------------

    // Custom queries for the user role ---------------------------------------

    // To get a user based on its name
    Optional<User> findByUsername(String username);

    // Custom queries for the admin role --------------------------------------

    // To get all of the users with the user role
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.dto.AdminDto(u.id, u.username, u.enabled)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 2 OR r.id = 3
        )
    """)
    List<AdminDto> getAllUsersWithUserRole();
    
    // To get a specific user who only has the user role 
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.dto.AdminDto(u.id, u.username, u.enabled)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 2 OR r.id = 3
        ) and u.id = ?1
    """)
    Optional<AdminDto> getUserWithUserRole(Long id);

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
    List<Long> getAllIdsWithAdminAndSuperAdminRole();

    // Custom queries for the super admin role --------------------------------------
    
    // To get all of the users who have the user role or the user and admin role
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.dto.SuperAdminDto(u.id, u.username, u.enabled, u.admin)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 3
        )
    """)
    List<SuperAdminDto> getAllUsersWithUserAndAdminRole();

    // To get a specific user who has the user role or the user and admin role
    @Query("""
        SELECT DISTINCT new com.alejandro.gestordenotas.dto.SuperAdminDto(u.id, u.username, u.enabled, u.admin)
        FROM User u
        WHERE u.id NOT IN (
            SELECT DISTINCT u2.id
            FROM User u2
            JOIN u2.roles r
            WHERE r.id = 3
        ) and u.id = ?1
    """)
    Optional<SuperAdminDto> getUserWithUserAndAdminRole(Long id);

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