package com.talentcloud.auth.service;

import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    public Mono<String> login(LoginRequest request) {
        // TODO: Implement real login with Keycloak token API
        return Mono.just("mock-token-for-" + request.getUsername());
    }

    public Mono<String> register(RegisterRequest request) {
        // TODO: Call Keycloak Admin API to create user
        return Mono.just("User registered successfully");
    }
}
