package com.alejandro.gestordenotas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.services.dto.UserDto;

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

}