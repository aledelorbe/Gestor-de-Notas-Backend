package com.alejandro.gestordenotas.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityJsonCreator {

    // When the method to deserialize is invoked, the method will search for in the 
    // json object the attribute called 'authority' attribute 'role' insted.
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
    
    }
}
