package com.profile_service.profile_service.controller;



import com.profile_service.profile_service.dto.UserRegistrationRequest;
import com.profile_service.profile_service.service.KeycloakService;
import com.profile_service.profile_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final KeycloakService keycloakService;

    public AuthController(UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Admin endpoint accessed successfully");
    }

    @GetMapping("/candidate/test")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
    public ResponseEntity<String> candidateEndpoint() {
        return ResponseEntity.ok("Candidate endpoint accessed successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());
        return userService.registerUser(request);
    }
}
