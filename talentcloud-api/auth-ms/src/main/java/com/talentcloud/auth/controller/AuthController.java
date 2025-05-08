package com.talentcloud.auth.controller;

import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import com.talentcloud.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest request) {
        // authService.login(request) already returns Mono<ResponseEntity<String>>
        return authService.login(request);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegisterRequest request) {
        // authService.register(request) already returns Mono<ResponseEntity<String>>
        return authService.register(request);
    }
}