package com.alejandro.gestordenotas;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.alejandro.gestordenotas.utils.UtilValidation;


// We use this class to create components in the test context
@TestConfiguration
public class TestConfig {
    
    // Create the component that represents the real UtilValidation class
    @Bean
    UtilValidation utilValidation() {
        return new UtilValidation();
    }

}
