package com.talentcloud.gatewayms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsConfigurationSource;

import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 1. Apply CORS configuration. The cast is no longer needed.
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF, as it's not needed for stateless APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 3. Define authorization rules for all routes
                .authorizeExchange(exchange -> exchange
                        // 3a. CRITICAL: Allow all CORS preflight OPTIONS requests to pass
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 3b. Define public endpoints that do not require a token
                        .pathMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()

                        // 3c. For all other requests, authentication is required
                        .anyExchange().authenticated()
                )

                // 4. Configure the gateway as an OAuth2 resource server to validate JWTs
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    // THE RETURN TYPE IS NOW THE REACTIVE CorsConfigurationSource
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Define the origins allowed to access the gateway (your frontend application)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://172.20.52.160:3000"));

        // Define the allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers. You can be more specific if needed.
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (e.g., cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // USE THE REACTIVE IMPLEMENTATION
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}