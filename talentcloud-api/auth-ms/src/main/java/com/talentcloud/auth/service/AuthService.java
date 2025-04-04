package com.talentcloud.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import com.talentcloud.auth.model.User;
import com.talentcloud.auth.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final String KEYCLOAK_ADMIN_URL = "http://localhost:8080/admin/realms/talentcloud/users";
    private final String KEYCLOAK_ROLES_URL = "http://localhost:8080/admin/realms/talentcloud/roles";
    private final KeycloakService keycloakService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuthService(KeycloakService keycloakService, RestTemplate restTemplate,
                       UserRepository userRepository, ObjectMapper objectMapper) {
        this.keycloakService = keycloakService;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> login(LoginRequest request) {
        // TODO: Implement real login with Keycloak token API
        return ResponseEntity.ok("mock-token-for-" + request.getUsername());
    }

    public ResponseEntity<String> register(RegisterRequest request) {
        try {
            logger.info("Starting user registration process for username: {}", request.getUsername());

            // Check if user already exists in PostgreSQL
            if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
                logger.warn("User already exists in database: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!");
            }

            // Get Keycloak Admin Token
            String adminToken = keycloakService.getAdminToken();
            logger.info("Obtained admin token from Keycloak");

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + adminToken);

            // Create Keycloak user payload
            Map<String, Object> keycloakUser = new HashMap<>();
            keycloakUser.put("username", request.getUsername());
            keycloakUser.put("email", request.getEmail());
            keycloakUser.put("enabled", true);

            // Set credentials
            Map<String, Object> credentials = new HashMap<>();
            credentials.put("type", "password");
            credentials.put("value", request.getPassword());
            credentials.put("temporary", false);
            keycloakUser.put("credentials", Collections.singletonList(credentials));

            // Step 1: Create the User in Keycloak
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(keycloakUser, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_ADMIN_URL, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to create user in Keycloak: {}", response.getBody());
                return ResponseEntity.status(response.getStatusCode()).body("Failed to register user: " + response.getBody());
            }

            logger.info("User created successfully in Keycloak");

            // Step 2: Retrieve the User ID from Keycloak
            ResponseEntity<String> usersResponse = restTemplate.exchange(
                    KEYCLOAK_ADMIN_URL + "?username=" + request.getUsername(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (!usersResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to retrieve user from Keycloak: {}", usersResponse.getBody());
                return ResponseEntity.status(usersResponse.getStatusCode()).body("User created but retrieval failed: " + usersResponse.getBody());
            }

            String userId = extractUserIdFromResponse(usersResponse.getBody());
            if (userId == null) {
                logger.error("Failed to extract user ID from Keycloak response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract user ID");
            }

            logger.info("Retrieved user ID from Keycloak: {}", userId);

            // Step 3: Get available roles from Keycloak
            ResponseEntity<String> rolesResponse = restTemplate.exchange(
                    KEYCLOAK_ROLES_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (!rolesResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to retrieve roles from Keycloak: {}", rolesResponse.getBody());
                return ResponseEntity.status(rolesResponse.getStatusCode()).body("Failed to retrieve roles");
            }

            // Find role ID for the requested role
            String roleId = findRoleId(rolesResponse.getBody(), request.getRole());
            if (roleId == null) {
                logger.error("Requested role not found: {}", request.getRole());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested role not found: " + request.getRole());
            }

            logger.info("Found role ID for role {}: {}", request.getRole(), roleId);

            // Step 4: Assign Role to User
            String roleAssignmentUrl = KEYCLOAK_ADMIN_URL + "/" + userId + "/role-mappings/realm";
            List<Map<String, Object>> rolesList = new ArrayList<>();
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("id", roleId);
            roleMap.put("name", request.getRole());
            rolesList.add(roleMap);

            HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(rolesList, headers);
            ResponseEntity<String> roleResponse = restTemplate.postForEntity(roleAssignmentUrl, roleRequest, String.class);

            if (!roleResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to assign role to user: {}", roleResponse.getBody());
                return ResponseEntity.status(roleResponse.getStatusCode())
                        .body("User created but role assignment failed: " + roleResponse.getBody());
            }

            logger.info("Role assigned successfully to user");

            // Step 5: Save user in PostgreSQL
            User user = new User();
            user.setKeycloakId(userId);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setRole(request.getRole().toLowerCase());
            userRepository.save(user);

            logger.info("User saved successfully in PostgreSQL database");

            return ResponseEntity.ok("User registered and role assigned successfully!");

        } catch (Exception e) {
            logger.error("Error during user registration process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to an internal error: " + e.getMessage());
        }

    }

    // Helper method to extract user ID
    private String extractUserIdFromResponse(String responseBody) {
        try {
            JsonNode userArray = objectMapper.readTree(responseBody);
            if (userArray.isArray() && userArray.size() > 0) {
                return userArray.get(0).get("id").asText();
            }
        } catch (Exception e) {
            logger.error("Error extracting user ID from response", e);
        }
        return null;
    }

    // Helper method to find role ID
    private String findRoleId(String rolesResponseBody, String roleName) {
        try {
            JsonNode rolesArray = objectMapper.readTree(rolesResponseBody);
            for (JsonNode role : rolesArray) {
                if (role.has("name") && role.get("name").asText().equals(roleName)) {
                    return role.get("id").asText();
                }
            }
        } catch (Exception e) {
            logger.error("Error finding role ID", e);
        }
        return null;
    }
}
