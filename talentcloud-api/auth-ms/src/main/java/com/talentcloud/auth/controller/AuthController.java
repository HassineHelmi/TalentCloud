package com.talentcloud.auth.controller;

import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.LogoutRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import com.talentcloud.auth.dto.UserProfileDto;
import com.talentcloud.auth.model.Role; // Import Role enum
import com.talentcloud.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors; // For stream operations

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@Valid @RequestBody LogoutRequest request) {
        return authService.logout(request);
    }


    @GetMapping("/profile")
    public Mono<ResponseEntity<UserProfileDto>> getProfile(@AuthenticationPrincipal Mono<Jwt> jwtMono) {
        return jwtMono.map(jwt -> {
            String id = jwt.getSubject();
            String username = jwt.getClaimAsString("preferred_username");
            String email = jwt.getClaimAsString("email");
            String firstName = jwt.getClaimAsString("given_name");
            String lastName = jwt.getClaimAsString("family_name");
            String fullName = jwt.getClaimAsString("name");

            // Extract roles as List<String> from JWT
            List<String> rawRoles = jwt.getClaimAsMap("realm_access") != null ?
                    (List<String>) ((Map<?, ?>) jwt.getClaim("realm_access")).get("roles") : Collections.emptyList();

            // Convert String roles to Role enum
            List<Role> roles = rawRoles.stream()
                    .map(roleName -> {
                        try {
                            // Assuming Keycloak roles match enum names directly (e.g., "ROLE_ADMIN")
                            return Role.valueOf(roleName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            // Log warning for unrecognized roles, or handle as needed
                            System.err.println("Warning: Unrecognized role from JWT: " + roleName);
                            return null; // Or a default role like Role.UNKNOWN if you define it
                        }
                    })
                    .filter(Objects::nonNull) // Filter out nulls if unrecognized roles return null
                    .collect(Collectors.toList());

            UserProfileDto profile = new UserProfileDto(id, username, email, firstName, lastName, fullName, roles);
            return ResponseEntity.ok(profile);
        });
    }

}