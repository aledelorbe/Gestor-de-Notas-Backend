package com.alejandro.gestordenotas.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alejandro.gestordenotas.entities.Role;

// The class that contains the data to be mocked in the service and controller methods
public class RoleData {
    
    public static final List<Long> idsValid = Arrays.asList(1L, 2L, 3L);

    public static Role createRole001() {
        return new Role(idsValid.get(0), "ROLE_USER");
    }

    public static Role createRole002() {
        return new Role(idsValid.get(0), "ROLE_ADMIN");
    }

    public static Role createRole003() {
        return new Role(idsValid.get(0), "ROLE_SUPER_ADMIN");
    }

    public static Set<Role> createRoles001() {
        return new HashSet<>(Arrays.asList(createRole001()));
    }

    public static Set<Role> createRoles002() {
        return new HashSet<>(Arrays.asList(createRole001(), createRole002()));
    }

        public static Set<Role> createRoles003() {
        return new HashSet<>(Arrays.asList(createRole001(), createRole002(), createRole003()));
    }

}
