package com.alejandro.gestordenotas.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    // ---------------------
    // Custom queries ------
    // ---------------------

    // To get a specific user based on its 'username'
    Optional<Role> findByUsername(String username);

}
