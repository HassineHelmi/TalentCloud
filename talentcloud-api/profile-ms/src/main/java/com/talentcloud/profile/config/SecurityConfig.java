package com.talentcloud.profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        Converter<Jwt, Collection<GrantedAuthority>> realmAccessRolesExtractor = jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return Collections.emptyList();
            }
            @SuppressWarnings("unchecked")
            List<String> rolesFromJwt = (List<String>) realmAccess.get("roles");
            if (rolesFromJwt == null) {
                return Collections.emptyList();
            }

            return rolesFromJwt.stream()
                    .map(roleName -> roleName.replace("ROLE_", "")) // Remove 'ROLE_' prefix
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        };
        // This is the standard converter for 'scope' or 'scp' claims.
        JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Combine authorities from both realm_access roles and scopes
        Converter<Jwt, Collection<GrantedAuthority>> combinedAuthoritiesConverter = jwt -> {
            Collection<GrantedAuthority> roles = realmAccessRolesExtractor.convert(jwt);
            Collection<GrantedAuthority> scopes = scopeAuthoritiesConverter.convert(jwt);

            List<GrantedAuthority> combinedAuthorities = new ArrayList<>();
            if (roles != null) {
                combinedAuthorities.addAll(roles);
            }
            if (scopes != null) {
                combinedAuthorities.addAll(scopes);
            }
            return combinedAuthorities;
        };

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(combinedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // This should be restricted to your frontend's domain in production
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Apply CORS configuration
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Any authenticated user can view their own profile
                        .requestMatchers(HttpMethod.GET, "/api/v1/profiles/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/profiles/me").authenticated()

                        // Candidates can manage their own details
                        .requestMatchers("/api/v1/candidates/**").hasAuthority("CANDIDATE")
                        .requestMatchers("/api/v1/educations/**").hasAuthority("CANDIDATE")
                        .requestMatchers("/api/v1/experiences/**").hasAuthority("CANDIDATE")
                        .requestMatchers("/api/v1/certifications/**").hasAuthority("CANDIDATE")
                        .requestMatchers("/api/v1/skills/**").hasAuthority("CANDIDATE")

                        // Clients can manage their details and view candidate listings
                        .requestMatchers("/api/v1/client/**").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/candidates/all", "/api/v1/candidates/{id}").hasAnyRole("CLIENT", "ADMIN")

                        // Admins have their own endpoints
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                        // Fallback: any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }
}
