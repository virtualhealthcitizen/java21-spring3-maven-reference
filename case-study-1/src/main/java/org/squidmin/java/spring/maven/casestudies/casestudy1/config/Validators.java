package org.squidmin.java.spring.maven.casestudies.casestudy1.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to provide a Validator bean for validating objects.
 * This is useful for ensuring data integrity and enforcing constraints.
 */
@Configuration
public class Validators {
    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
