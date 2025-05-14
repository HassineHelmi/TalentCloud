package com.talentcloud.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration; // This class is used by both WebMVC and WebFlux
import org.springframework.web.cors.reactive.CorsConfigurationSource; // WebFlux specific
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource; // WebFlux specific

import java.util.Arrays;
import java.util.List;

// Removed unused imports for JWT and Role for this specific CORS configuration example
// import com.talentcloud.auth.model.Role;
// import org.springframework.core.convert.converter.Converter;
// import org.springframework.security.authentication.AbstractAuthenticationToken;
// import org.springframework.security.oauth2.jwt.*;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
// import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
// import reactor.core.publisher.Mono;
// import org.springframework.beans.factory.annotation.Value;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // Apply CORS configuration using the bean defined below
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Your existing CSRF config
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/v3/api-docs.yaml").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth/register").permitAll()
                        .pathMatchers("/api/auth/login").permitAll()
                        .anyExchange().permitAll() // Your existing authorization rule
                );
        // OAuth2 resource server config is commented out as per your file.

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(List.of(
                "http://172.20.52.160:3000", // Your Next.js frontend development URL
                "https://your-production-frontend-domain.com" // Replace with your actual production domain
        ));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allowed request headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",        // For JWT token
                "Content-Type",         // For request body type (e.g., application/json)
                "X-Requested-With",     // Common header for AJAX requests
                "Accept",               // To specify acceptable response types
                "Origin",               // Standard CORS header
                "Access-Control-Request-Method", // Standard CORS header
                "Access-Control-Request-Headers" // Standard CORS header
        ));

        // Headers exposed to the client-side JavaScript code
        // If your client needs to read specific headers from the response (e.g., a custom header or Authorization for some flows)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
                // "X-Custom-Response-Header" // Example if you have custom ones
        ));

        // IMPORTANT: This must be true if your frontend sends credentials (cookies, Authorization header)
        configuration.setAllowCredentials(true);

        // How long the results of a pre-flight request (OPTIONS) can be cached by the client (in seconds)
        configuration.setMaxAge(3600L); // e.g., 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all paths "/**"
        // You can make this more specific if needed, e.g., "/api/**"
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Commented out JWT beans as per your original file
    /*
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") // Make sure you have this property if you re-enable
    private String jwkSetUri;

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Configure jwtAuthenticationConverter if needed (e.g., for roles/authorities extraction)
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
    */
}