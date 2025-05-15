package com.talentcloud.auth.controller;

import com.nimbusds.jwt.JWT;
import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.LogoutRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import com.talentcloud.auth.dto.UserProfileDto;
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
            List<String> roles = jwt.getClaimAsMap("realm_access") != null ?
                    (List<String>) ((Map<?, ?>) jwt.getClaim("realm_access")).get("roles") : Collections.emptyList();

            UserProfileDto profile = new UserProfileDto(id, username, email, firstName, lastName, fullName, roles);
            return ResponseEntity.ok(profile);
        });
    }

}