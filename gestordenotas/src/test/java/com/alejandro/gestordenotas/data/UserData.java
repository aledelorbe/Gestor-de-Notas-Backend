package com.alejandro.gestordenotas.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alejandro.gestordenotas.entities.User;

// The class that contains the data to be mocked in the service and controller methods
public class UserData {
    
    public static final List<Long> idsValid = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

    public static User createUser001() {
        return new User(idsValid.get(0), "alejandro", "ale123", true, true, NoteData.createNotes001(), RoleData.createRoles003());
    }

    public static User createUser002() {
        return new User(idsValid.get(1), "fernando", "fer123", true, true, NoteData.createNotes002(), RoleData.createRoles002());
    }

    public static User createUser003() {
        return new User(idsValid.get(2), "celia", "celia123", true, true, NoteData.createNotes003(), RoleData.createRoles002());
    }

    public static User createUser004() {
        return new User(idsValid.get(3), "jorge", "jorge123", false, true, NoteData.createNotes004(), RoleData.createRoles001());
    }

    public static User createUser005() {
        return new User(idsValid.get(4), "rayas", "rayas123", false, true, new ArrayList<>(), RoleData.createRoles001());
    }

    public static User createUser006() {
        return new User(idsValid.get(5), "pancha", "pancha123", false, false, NoteData.createNotes005(), RoleData.createRoles001());
    }

}
