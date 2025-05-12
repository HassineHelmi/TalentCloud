package com.talentcloud.auth.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentcloud.auth.dto.LoginRequest;
import com.talentcloud.auth.dto.RegisterRequest;
import com.talentcloud.auth.model.User;
import com.talentcloud.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
 
    @Value("${KEYCLOAK_URL}")
    private String KEYCLOAK_BASE_URL;
    @Value("${KEYCLOAK_REALM}")
    private String KEYCLOAK_REALM;  
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final String KEYCLOAK_TOKEN_URL = KEYCLOAK_BASE_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token";
    private final String KEYCLOAK_ADMIN_URL = KEYCLOAK_BASE_URL + "/admin/realms/" + KEYCLOAK_REALM + "/users";
    private final String KEYCLOAK_ROLES_URL = KEYCLOAK_BASE_URL + "/admin/realms/" + KEYCLOAK_REALM + "/roles";

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

    public Mono<ResponseEntity<String>> login(LoginRequest request) {
        return Mono.fromCallable(() -> {
            try {
                logger.info("Attempting login for user: {}", request.getUsername());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "password");
                body.add("client_id", "public-client");
                body.add("client_secret", clientSecret);
                body.add("username", request.getUsername());
                body.add("password", request.getPassword());
                body.add("scope", "openid");

                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

                logger.debug("Token request URL: {}", KEYCLOAK_TOKEN_URL);
                logger.debug("Token request body: {}", body);

                ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_TOKEN_URL, entity, String.class);

                JsonNode tokenJson = objectMapper.readTree(response.getBody());
                String accessToken = tokenJson.get("access_token").asText();

                logger.info("Login successful for user: {}", request.getUsername());
                return ResponseEntity.ok(accessToken);

            } catch (HttpStatusCodeException e) {
                logger.error("Login failed for user {}: {} - {}", request.getUsername(), e.getStatusCode(), e.getResponseBodyAsString(), e);
                return ResponseEntity.status(e.getStatusCode())
                        .body("Login failed: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                logger.error("Login failed for user {}: {}", request.getUsername(), e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Login failed due to an internal error: " + e.getMessage());
            }
        });
    }

    public Mono<ResponseEntity<String>> register(RegisterRequest request) {
        logger.info("Starting user registration process for username: {}", request.getUsername());

        return userRepository.existsByUsername(request.getUsername())
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        logger.warn("Username already exists in database: {}", request.getUsername());
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!"));
                    }
                    return userRepository.existsByEmail(request.getEmail())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    logger.warn("Email already exists in database: {}", request.getEmail());
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists!"));
                                }

                                return Mono.fromCallable(() -> {
                                    try {
                                        String adminToken = keycloakService.getAdminToken();
                                        logger.info("Obtained admin token from Keycloak");

                                        HttpHeaders headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.set("Authorization", "Bearer " + adminToken);

                                        Map<String, Object> keycloakUser = new HashMap<>();
                                        keycloakUser.put("username", request.getUsername());
                                        keycloakUser.put("email", request.getEmail());
                                        keycloakUser.put("enabled", true);
                                        keycloakUser.put("firstName", request.getFirstName());
                                        keycloakUser.put("lastName", request.getLastName());

                                        Map<String, Object> credentials = new HashMap<>();
                                        credentials.put("type", "password");
                                        credentials.put("value", request.getPassword());
                                        credentials.put("temporary", false);
                                        keycloakUser.put("credentials", Collections.singletonList(credentials));

                                        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(keycloakUser, headers);
                                        restTemplate.postForEntity(KEYCLOAK_ADMIN_URL, requestEntity, String.class); // Original response stored in 'response', but not directly used below for this line.

                                        logger.info("User created successfully in Keycloak");

                                        ResponseEntity<String> usersResponse = restTemplate.exchange(
                                                KEYCLOAK_ADMIN_URL + "?username=" + request.getUsername(),
                                                HttpMethod.GET,
                                                new HttpEntity<>(headers),
                                                String.class
                                        );
                                        logger.info("User retrieval response from Keycloak: {}", usersResponse.getStatusCode());

                                        String keycloakActualUserId = extractUserIdFromResponse(usersResponse.getBody()); // Renamed for clarity
                                        if (keycloakActualUserId == null) {
                                            logger.error("Failed to extract user ID from Keycloak response");
                                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                    .body("Failed to extract user ID after creation in Keycloak");
                                        }
                                        logger.info("Retrieved user ID from Keycloak: {}", keycloakActualUserId);

                                        ResponseEntity<String> rolesResponse = restTemplate.exchange(
                                                KEYCLOAK_ROLES_URL,
                                                HttpMethod.GET,
                                                new HttpEntity<>(headers),
                                                String.class
                                        );

                                        String roleId = findRoleId(rolesResponse.getBody(), request.getRole());
                                        if (roleId == null) {
                                            logger.error("Requested role not found: {}", request.getRole());
                                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                    .body("Requested role not found: " + request.getRole());
                                        }
                                        logger.info("Found role ID for role {}: {}", request.getRole(), roleId);

                                        String roleAssignmentUrl = KEYCLOAK_ADMIN_URL + "/" + keycloakActualUserId + "/role-mappings/realm";
                                        List<Map<String, Object>> rolesList = new ArrayList<>();
                                        Map<String, Object> roleMap = new HashMap<>();
                                        roleMap.put("id", roleId);
                                        roleMap.put("name", request.getRole());
                                        rolesList.add(roleMap);

                                        HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(rolesList, headers);
                                        ResponseEntity<String> roleAssignmentResponseEntity = restTemplate.postForEntity( // Renamed variable
                                                roleAssignmentUrl, roleRequest, String.class);

                                        logger.info("Role assignment response: {}", roleAssignmentResponseEntity.getStatusCode()); // Used new variable name
                                        logger.info("Role assigned successfully to user");

                                        // Create and save user in our database
                                        User user = new User();
                                        // ID is NOT set here, database will auto-generate it.
                                        user.setKeycloakId(keycloakActualUserId); // Use the extracted Keycloak user ID
                                        user.setUsername(request.getUsername());
                                        user.setEmail(request.getEmail());
                                        user.setRole(request.getRole().toUpperCase());
                                        // You might want to also save firstName and lastName in your database
                                        // user.setFirstName(request.getFirstName());
                                        // user.setLastName(request.getLastName());

                                        userRepository.save(user).subscribe(
                                                savedUser -> logger.info("User saved successfully in PostgreSQL database with ID: {} and Username: {}",
                                                        savedUser.getId(), savedUser.getUsername()), // Updated log message
                                                error -> logger.error("Error saving user to PostgreSQL: ", error)
                                        );

                                        return ResponseEntity.ok("User registered and role assigned successfully!");

                                    } catch (HttpStatusCodeException e) {
                                        logger.error("Error during user registration process for {}: {} - {}",
                                                request.getUsername(), e.getStatusCode(), e.getResponseBodyAsString(), e);
                                        return ResponseEntity.status(e.getStatusCode())
                                                .body("Registration failed: " + e.getResponseBodyAsString());
                                    } catch (Exception e) {
                                        logger.error("Error during user registration process for {}:", request.getUsername(), e);
                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Registration failed due to an internal error: " + e.getMessage());
                                    }
                                });
                            });
                });
    }

    private String extractUserIdFromResponse(String responseBody) {
        try {
            JsonNode userArray = objectMapper.readTree(responseBody);
            if (userArray.isArray() && !userArray.isEmpty()) {
                return userArray.get(0).get("id").asText();
            } else {
                logger.warn("User array is empty or not an array in Keycloak response: {}", responseBody);
            }
        } catch (Exception e) {
            logger.error("Error extracting user ID from response: {}", responseBody, e);
        }
        return null;
    }

    private String findRoleId(String rolesResponseBody, String roleName) {
        try {
            JsonNode rolesArray = objectMapper.readTree(rolesResponseBody);
            if (rolesArray.isArray()) {
                for (JsonNode role : rolesArray) {
                    if (role.has("name") && role.get("name").asText().equalsIgnoreCase(roleName)) {
                        return role.get("id").asText();
                    }
                }
            } else {
                logger.warn("Roles response is not an array: {}", rolesResponseBody);
            }
        } catch (Exception e) {
            logger.error("Error finding role ID for role {}: {}", roleName, rolesResponseBody, e);
        }
        return null;
    }
}
