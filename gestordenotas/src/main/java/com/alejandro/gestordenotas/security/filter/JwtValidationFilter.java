package com.alejandro.gestordenotas.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.alejandro.gestordenotas.security.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.alejandro.gestordenotas.security.TokenJwtConfig.*;

// This filter is executed before Spring allows access to protected resources.
public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Extract the header because there is the token.
        String header = request.getHeader(HEADER_AUTHORIZATION);

        // If the page is public it will not have a header or if the header doesn't have the standard word (Bearer )
        // it will finish the method
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Extract the token without the prefix
        String token = header.replace(PREFIX_TOKEN, "");
        try {
            // Valid the token and get the subject and roles
            // If the token is invalid or expired it will be fired an exception
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            String usename = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities");

            // The roles are deserialized into a collection of 'SimpleGrantedAuthority' objects
            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class)
            );

            // Authenticate the request 
            // (This allows access to protected resources)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(usename, null, authorities);
            SecurityContextHolder .getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response); // continue with the other filters
        } catch (JwtException e) {
            // Creation of body response
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es invalido!");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }
    }
}
