package com.talentcloud.auth.config;

import org.springframework.beans.factory.annotation.Value; // Required for jwkSetUri
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter; // Required for grantedAuthoritiesExtractor
import org.springframework.security.authentication.AbstractAuthenticationToken; // Required for grantedAuthoritiesExtractor
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt; // Required for grantedAuthoritiesExtractor
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder; // Required for reactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder; // Required for reactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // Required
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter; // Required
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono; // Required for grantedAuthoritiesExtractor

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // This value MUST be present in your application.properties or application.yml
    // Example: spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://172.20.52.160:30080/realms/talent/protocol/openid-connect/certs
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Publicly accessible paths
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/v3/api-docs.yaml").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth/register").permitAll()
                        .pathMatchers("/api/auth/login").permitAll()
                        // Secure your new profile endpoint
                        .pathMatchers("/api/auth/profile").authenticated() // Or /api/auth/user if you named it that
                        // Secure all other unspecified exchanges by default - good practice!
                        .anyExchange().authenticated()
                )
                // THIS IS THE CRUCIAL PART: Configure your app as an OAuth2 Resource Server
                // It will validate incoming JWTs (Bearer Tokens)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtSpec -> jwtSpec
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor()) // For extracting roles/authorities
                                .jwtDecoder(reactiveJwtDecoder()) // For decoding and validating the JWT signature
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Using patterns for localhost flexibility during development
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"
                // "https://your-production-frontend-domain.com" // Add your production domain here
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Bean to convert JWT claims to Spring Security's AbstractAuthenticationToken (including authorities)
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // If you need to customize how roles/authorities are extracted from the JWT,
        // you would configure the jwtAuthenticationConverter here.
        // For example, to read roles from "realm_access.roles":
        //
        // JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles"); // Or whatever your roles claim is
        // grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Optional: adds "ROLE_" prefix
        // jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    // Bean to decode and validate JWTs using the JWK Set URI from Keycloak
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
}