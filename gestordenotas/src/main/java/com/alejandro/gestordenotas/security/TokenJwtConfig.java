package com.alejandro.gestordenotas.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

// This class contain many constant variables
// These variables are used in many files
public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    
}
