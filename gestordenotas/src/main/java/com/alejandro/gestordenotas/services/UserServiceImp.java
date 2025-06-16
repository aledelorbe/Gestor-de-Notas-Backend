package com.alejandro.gestordenotas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.alejandro.gestordenotas.dto.UserDto;
import com.alejandro.gestordenotas.entities.Note;
import com.alejandro.gestordenotas.entities.Role;
import com.alejandro.gestordenotas.entities.User;
import com.alejandro.gestordenotas.repositories.RoleRepository;
import com.alejandro.gestordenotas.repositories.UserRepository;

@Service
public class UserServiceImp implements UserService {

    // To inject the repository dependency.
    @Autowired
    private UserRepository repository;

    // To inject the repository dependency.
    @Autowired
    private RoleRepository roleRepository;

    // To be able to encrypt passwords
    @Autowired
    private PasswordEncoder passwordEncoder;

    // -----------------------------
    // Methods for user entity
    // -----------------------------

    // To list all of users (records) in the table 'users'
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll(); // cast because the method findAll returns an iterable.
    }

    // To get a specific user based on its id
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    // To save a new user in the db
    // This method is a 'join point'
    @Override
    @Transactional
    public User save(User user) {
        // Search for the rol called 'role_user' in the table 'role'
        // (All of the users register must have at least this role) *******
        Set<Role> roles = new HashSet<>();
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        // If this role is present then add the role to list of roles
        optionalRoleUser.ifPresent(roles::add);

        // But only if the new user must be admin then add this role to list of roles
        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        // Add all of the roles to new user
        user.setRoles(roles);
        // Encrypt the password of the user and save the user in the db
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

    // To update a specific user based on its id
    @Override
    @Transactional
    public Optional<User> update(Long id, User user) {
        // Find a specific user
        Optional<User> optionalUser = repository.findById(id);

        // If the user is present then...
        if (optionalUser.isPresent()) {
            // update that record and return an optional value
            User userDb = optionalUser.get();

            userDb.setUsername(user.getUsername());
            userDb.setPassword(passwordEncoder.encode(user.getPassword()));

            return Optional.ofNullable(repository.save(userDb));
        }

        return optionalUser;
    }

    // To delete a specific user based on its id
    @Override
    @Transactional
    public Optional<User> deleteById(Long id) {
        // Search for a specific user
        Optional<User> optionalUser = repository.findById(id);

        // If the user is present then delete that user
        optionalUser.ifPresent(userDb -> {
            repository.deleteById(id);
        });

        return optionalUser;
    }

    // To convert a specific user into an admin user
    @Override
    @Transactional
    public User convertUserIntoAdmin(User userDb) {
        // Find the specific role
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");

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

        return repository.save(userDb);
    }

    // Methods for super admin role -----------------

    // To get all of the users with the role 'user'
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersWithRoleUserAndAdmin() {
        return repository.getAllUsersWithRoleUserAndAdmin();
    }

    // Methods aux ----------------------------------
    
    // To get all of the id's of users with role 'admin' and 'super admin'
    @Override
    @Transactional(readOnly = true)
    public List<Long> getAllIdWithRoleAdminAndSuperAdmin() {
        return repository.getAllIdWithRoleAdminAndSuperAdmin();
    }
    
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

    // -----------------------------
    // Methods for custom queries of user entity
    // -----------------------------

    // To get all the pets of certain user
    @Override
    @Transactional(readOnly = true)
    public List<Note> getNotesByUserId(Long id_user) {
        return repository.getNotesByUserId(id_user);
    }

    // To get a certain user based on its username
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return repository.findByUsername(username);
    }

}