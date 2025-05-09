package com.talentcloud.profile.config; // Or your actual config package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_ALLOWED_PATHS = {
            "/swagger-ui.html",          // The path you configured
            "/swagger-ui/**",
            "/v3/api-docs/**",           // The OpenAPI spec endpoint
            "/swagger-resources/**",
            // "/webjars/**" // Not always needed with springdoc if it serves its own
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_ALLOWED_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())); // Or your specific JWT config
        // .csrf(csrf -> csrf.disable()); // Consider CSRF implications if you disable it

        return http.build();
    }
}