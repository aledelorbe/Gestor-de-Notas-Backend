package com.alejandro.gestordenotas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.alejandro.gestordenotas.security.filter.JwtAuthenticationFilter;
import com.alejandro.gestordenotas.security.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    // To be able to encrypt passwords
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // To use the login
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Method to config the security rules
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authz) -> authz
                // Endpoints for user role
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasRole("ADMIN")
                // .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                // Endpoints for admin role
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("USER") 
                .requestMatchers(HttpMethod.POST, "/api/users/{userId}/notes").hasRole("USER") // Notes
                .requestMatchers(HttpMethod.PATCH, "/api/users/{userId}/notes/{noteId}").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}/notes/{noteId}").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/users/{id_user}/notes").hasRole("USER")
                // Endpoint public
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // login
                .addFilter(new JwtValidationFilter(authenticationManager())) // to validate the token
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
