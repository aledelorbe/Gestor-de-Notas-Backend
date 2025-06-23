package com.alejandro.gestordenotas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.dto.SuperAdminDto;
import com.alejandro.gestordenotas.entities.Role;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.RoleRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;

@Service
public class SuperAdminServiceImp implements SuperAdminService {

    // To inject the repository dependency.
    @Autowired
    private UserRepository repository;

    // To inject the repository dependency.
    @Autowired
    private RoleRepository roleRepository;

    // -----------------------------
    // Methods for the super admin role
    // -----------------------------

    // To get all of the users with the role 'user' and 'admin'
    @Override
    @Transactional(readOnly = true)
    public List<SuperAdminDto> getAllUsersWithUserAndAdminRole() {
        return repository.getAllUsersWithUserAndAdminRole();
    }

    // To get a specific user with the user role based on their user ID
    @Override
    @Transactional(readOnly = true)
    public Optional<SuperAdminDto> getUserWithUserAndAdminRole(Long id) {
        return repository.getUserWithUserAndAdminRole(id);
    }

    // To convert a specific user into an admin user
    @Override
    @Transactional
    public Optional<User> addRemoveAdminRoleFromUser(Long id) {

        // Search for a specific user
        Optional<User> optionalUser = repository.findById(id);

        if ( optionalUser.isPresent() ) {
            
            // Find the specific role
            Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
            User userDb = optionalUser.get();

            // If the role is present then...
            if (optionalRole.isPresent()) {

                boolean hasRole = userDb.getRoles().stream().filter(role -> "ROLE_ADMIN".equals(role.getName())).findFirst()
                        .isPresent();

                if (hasRole) {
                    // remove this role to this user and set with value false the attribute admin
                    userDb.getRoles().remove(optionalRole.get());
                    userDb.setAdmin(false);
                } else {
                    // add this role to this user and set with value true the attribute admin
                    userDb.getRoles().add(optionalRole.get());
                    userDb.setAdmin(true);
                }
            }

            return Optional.of(repository.save(userDb));
        }

        return optionalUser;
    }

    // Methods aux ----------------------------------

    // To know if the user ID is the same as the super admin ID
    @Override
    @Transactional(readOnly = true)
    public boolean isSuperAdmin(Long id) {

        boolean result = false;

        if (id == repository.getIdOfSuperAdmin()) {
            result = true;
        }

        return result;
    }

}
