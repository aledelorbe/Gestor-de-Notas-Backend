package com.alejandro.gestordenotas.utils;

import static com.alejandro.gestordenotas.security.TokenJwtConfig.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alejandro.gestordenotas.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Component
public class JwtTokenUtil {

    public String createToken(User user) {
        try {
            // Serialize roles as authorities
            List<Map<String, String>> authorities = user.getRoles().stream()
                    .map(role -> Map.of("authority", role.getName()))
                    .toList();

            String authoritiesJson = new ObjectMapper().writeValueAsString(authorities);

            Claims claims = Jwts.claims()
                    .add("authorities", authoritiesJson)
                    .add("username", user.getUsername())
                    .build();

            return Jwts.builder()
                    .subject(user.getUsername())
                    .claims(claims)
                    .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                    .issuedAt(new Date())
                    .signWith(SECRET_KEY)
                    .compact();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error when generate the token JWT", e);
        }
    }

}