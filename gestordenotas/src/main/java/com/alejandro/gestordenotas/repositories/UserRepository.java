package com.alejandro.gestordenotas.repositories;


import org.springframework.data.repository.CrudRepository;

import com.alejandro.gestordenotas.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {


}