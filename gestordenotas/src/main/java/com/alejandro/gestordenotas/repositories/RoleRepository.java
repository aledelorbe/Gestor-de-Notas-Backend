package com.alejandro.gestordenotas.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    // ---------------------
    // Custom queries ------
    // ---------------------

    // To get a specific role based on its role name
    Optional<Role> findByName(String name);

}
