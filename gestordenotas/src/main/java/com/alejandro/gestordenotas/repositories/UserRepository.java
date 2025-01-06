package com.alejandro.gestordenotas.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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

}