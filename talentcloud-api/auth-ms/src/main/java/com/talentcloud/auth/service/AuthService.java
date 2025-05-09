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
import reactor.core.publisher.Mono; // Import Mono

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

    // Modified to return Mono<ResponseEntity<String>>
    public Mono<ResponseEntity<String>> login(LoginRequest request) {
        return Mono.fromCallable(() -> { // Wrap blocking code
            try {
                logger.info("Attempting login for user: {}", request.getUsername());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "password");
                body.add("client_id", "public-client"); // Make sure this client ID is correct and configured in Keycloak
                body.add("username", request.getUsername());
                body.add("password", request.getPassword());

                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

                String tokenUrl = "http://localhost:8080/realms/talentcloud/protocol/openid-connect/token";

                ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);

                // ResponseEntity.ok() itself will handle 2xx.
                // For non-2xx, RestTemplate throws an exception (e.g., HttpClientErrorException, HttpServerErrorException)
                // which will be caught by the outer catch block.

                JsonNode tokenJson = objectMapper.readTree(response.getBody());
                String accessToken = tokenJson.get("access_token").asText();

                logger.info("Login successful for user: {}", request.getUsername());
                return ResponseEntity.ok(accessToken);

            } catch (HttpStatusCodeException e) { // Catch specific RestTemplate exceptions
                logger.error("Login failed for user {}: {} - {}", request.getUsername(), e.getStatusCode(), e.getResponseBodyAsString(), e);
                return ResponseEntity.status(e.getStatusCode())
                        .body("Login failed: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                logger.error("Login failed for user {}:", request.getUsername(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Login failed due to an internal error: " + e.getMessage());
            }
        });
    }

    // Modified to return Mono<ResponseEntity<String>>
    public Mono<ResponseEntity<String>> register(RegisterRequest request) {
        return Mono.fromCallable(() -> { // Wrap blocking code
            try {
                logger.info("Starting user registration process for username: {}", request.getUsername());

                // Check if user already exists in PostgreSQL (this is still blocking)
                // For a fully reactive approach with R2DBC, this check would also be reactive.
                // However, for now, we keep it within fromCallable.
                if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
                    logger.warn("User already exists in database: {}", request.getUsername());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!");
                }

                // Get Keycloak Admin Token
                String adminToken = keycloakService.getAdminToken(); // This is also blocking
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

                Map<String, Object> credentials = new HashMap<>();
                credentials.put("type", "password");
                credentials.put("value", request.getPassword());
                credentials.put("temporary", false);
                keycloakUser.put("credentials", Collections.singletonList(credentials));

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(keycloakUser, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_ADMIN_URL, requestEntity, String.class);

                // RestTemplate throws an exception for non-2xx responses, handled by HttpStatusCodeException catch.
                logger.info("User created successfully in Keycloak");

                ResponseEntity<String> usersResponse = restTemplate.exchange(
                        KEYCLOAK_ADMIN_URL + "?username=" + request.getUsername(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                logger.info("User retrieval response from Keycloak: {}", usersResponse.getStatusCode());


                String userId = extractUserIdFromResponse(usersResponse.getBody());
                if (userId == null) {
                    logger.error("Failed to extract user ID from Keycloak response");
                    // Potentially roll back Keycloak user creation or log for manual intervention
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract user ID after creation in Keycloak");
                }
                logger.info("Retrieved user ID from Keycloak: {}", userId);

                ResponseEntity<String> rolesResponse = restTemplate.exchange(
                        KEYCLOAK_ROLES_URL,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );

                String roleId = findRoleId(rolesResponse.getBody(), request.getRole());
                if (roleId == null) {
                    logger.error("Requested role not found: {}", request.getRole());
                    // Potentially roll back Keycloak user creation
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested role not found: " + request.getRole());
                }
                logger.info("Found role ID for role {}: {}", request.getRole(), roleId);

                String roleAssignmentUrl = KEYCLOAK_ADMIN_URL + "/" + userId + "/role-mappings/realm";
                List<Map<String, Object>> rolesList = new ArrayList<>();
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", roleId);
                roleMap.put("name", request.getRole()); // Ensure this matches the role name in Keycloak
                rolesList.add(roleMap);

                HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(rolesList, headers);
                ResponseEntity<String> roleResponse = restTemplate.postForEntity(roleAssignmentUrl, roleRequest, String.class);

                logger.info("Role assignment response: {}", roleResponse.getStatusCode());
                logger.info("Role assigned successfully to user");

                User user = new User();
                user.setKeycloakId(userId);
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setRole(request.getRole().toUpperCase()); // Consistent casing with your Role enum
                userRepository.save(user).subscribe( // Handle the Mono returned by R2DBC save
                        savedUser -> logger.info("User saved successfully in PostgreSQL database: {}", savedUser.getUsername()),
                        error -> logger.error("Error saving user to PostgreSQL: ", error)
                );


                return ResponseEntity.ok("User registered and role assigned successfully!");

            } catch (HttpStatusCodeException e) {
                logger.error("Error during user registration process for {}: {} - {}", request.getUsername(), e.getStatusCode(), e.getResponseBodyAsString(), e);
                // Consider more specific error messages or rollback mechanisms
                return ResponseEntity.status(e.getStatusCode())
                        .body("Registration failed: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                logger.error("Error during user registration process for {}:", request.getUsername(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Registration failed due to an internal error: " + e.getMessage());
            }
        });
    }

    private String extractUserIdFromResponse(String responseBody) {
        try {
            JsonNode userArray = objectMapper.readTree(responseBody);
            if (userArray.isArray() && !userArray.isEmpty()) { // Check if array is not empty
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
                    if (role.has("name") && role.get("name").asText().equalsIgnoreCase(roleName)) { // Case-insensitive match might be safer
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




//package com.talentcloud.auth.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.talentcloud.auth.dto.LoginRequest;
//import com.talentcloud.auth.dto.RegisterRequest;
//import com.talentcloud.auth.model.User;
//import com.talentcloud.auth.repository.UserRepository;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.slf4j.Logger;
//import org.springframework.http.*;
//import com.fasterxml.jackson.databind.JsonNode;
//
//import java.util.*;
//
//@Service
//public class AuthService {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
//
//    private final String KEYCLOAK_ADMIN_URL = "http://localhost:8080/admin/realms/talentcloud/users";
//    private final String KEYCLOAK_ROLES_URL = "http://localhost:8080/admin/realms/talentcloud/roles";
//    private final KeycloakService keycloakService;
//    private final RestTemplate restTemplate;
//    private final UserRepository userRepository;
//    private final ObjectMapper objectMapper;
//
//    public AuthService(KeycloakService keycloakService, RestTemplate restTemplate,
//                       UserRepository userRepository, ObjectMapper objectMapper) {
//        this.keycloakService = keycloakService;
//        this.restTemplate = restTemplate;
//        this.userRepository = userRepository;
//        this.objectMapper = objectMapper;
//    }
//
//    public ResponseEntity<String> login(LoginRequest request) {
//            try {
//                logger.info("Attempting login for user: {}", request.getUsername());
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//                body.add("grant_type", "password");
//                body.add("client_id", "your-client-id");
//                body.add("username", request.getUsername());
//                body.add("password", request.getPassword());
//
//                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
//
//                String tokenUrl = "http://localhost:8080/realms/talentcloud/protocol/openid-connect/token";
//
//                ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);
//
//                if (!response.getStatusCode().is2xxSuccessful()) {
//                    logger.error("Failed to login user: {}", response.getBody());
//                    return ResponseEntity.status(response.getStatusCode())
//                            .body("Login failed: " + response.getBody());
//                }
//
//                JsonNode tokenJson = objectMapper.readTree(response.getBody());
//                String accessToken = tokenJson.get("access_token").asText();
//
//                logger.info("Login successful for user: {}", request.getUsername());
//                return ResponseEntity.ok(accessToken);
//
//            } catch (Exception e) {
//                logger.error("Login failed", e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Login failed due to an internal error: " + e.getMessage());
//            }
//    }
//
//    public ResponseEntity<String> register(RegisterRequest request) {
//        try {
//            logger.info("Starting user registration process for username: {}", request.getUsername());
//
//            // Check if user already exists in PostgreSQL
//            if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
//                logger.warn("User already exists in database: {}", request.getUsername());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!");
//            }
//
//            // Get Keycloak Admin Token
//            String adminToken = keycloakService.getAdminToken();
//            logger.info("Obtained admin token from Keycloak");
//
//            // Set headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + adminToken);
//
//            // Create Keycloak user payload
//            Map<String, Object> keycloakUser = new HashMap<>();
//            keycloakUser.put("username", request.getUsername());
//            keycloakUser.put("email", request.getEmail());
//            keycloakUser.put("enabled", true);
//
//            // Set credentials
//            Map<String, Object> credentials = new HashMap<>();
//            credentials.put("type", "password");
//            credentials.put("value", request.getPassword());
//            credentials.put("temporary", false);
//            keycloakUser.put("credentials", Collections.singletonList(credentials));
//
//            // Step 1: Create the User in Keycloak
//            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(keycloakUser, headers);
//            ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_ADMIN_URL, requestEntity, String.class);
//
//            if (!response.getStatusCode().is2xxSuccessful()) {
//                logger.error("Failed to create user in Keycloak: {}", response.getBody());
//                return ResponseEntity.status(response.getStatusCode()).body("Failed to register user: " + response.getBody());
//            }
//
//            logger.info("User created successfully in Keycloak");
//
//            // Step 2: Retrieve the User ID from Keycloak
//            ResponseEntity<String> usersResponse = restTemplate.exchange(
//                    KEYCLOAK_ADMIN_URL + "?username=" + request.getUsername(),
//                    HttpMethod.GET,
//                    new HttpEntity<>(headers),
//                    String.class
//            );
//
//            if (!usersResponse.getStatusCode().is2xxSuccessful()) {
//                logger.error("Failed to retrieve user from Keycloak: {}", usersResponse.getBody());
//                return ResponseEntity.status(usersResponse.getStatusCode()).body("User created but retrieval failed: " + usersResponse.getBody());
//            }
//
//            String userId = extractUserIdFromResponse(usersResponse.getBody());
//            if (userId == null) {
//                logger.error("Failed to extract user ID from Keycloak response");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract user ID");
//            }
//
//            logger.info("Retrieved user ID from Keycloak: {}", userId);
//
//            // Step 3: Get available roles from Keycloak
//            ResponseEntity<String> rolesResponse = restTemplate.exchange(
//                    KEYCLOAK_ROLES_URL,
//                    HttpMethod.GET,
//                    new HttpEntity<>(headers),
//                    String.class
//            );
//
//            if (!rolesResponse.getStatusCode().is2xxSuccessful()) {
//                logger.error("Failed to retrieve roles from Keycloak: {}", rolesResponse.getBody());
//                return ResponseEntity.status(rolesResponse.getStatusCode()).body("Failed to retrieve roles");
//            }
//
//            // Find role ID for the requested role
//            String roleId = findRoleId(rolesResponse.getBody(), request.getRole());
//            if (roleId == null) {
//                logger.error("Requested role not found: {}", request.getRole());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested role not found: " + request.getRole());
//            }
//
//            logger.info("Found role ID for role {}: {}", request.getRole(), roleId);
//
//            // Step 4: Assign Role to User
//            String roleAssignmentUrl = KEYCLOAK_ADMIN_URL + "/" + userId + "/role-mappings/realm";
//            List<Map<String, Object>> rolesList = new ArrayList<>();
//            Map<String, Object> roleMap = new HashMap<>();
//            roleMap.put("id", roleId);
//            roleMap.put("name", request.getRole());
//            rolesList.add(roleMap);
//
//            HttpEntity<List<Map<String, Object>>> roleRequest = new HttpEntity<>(rolesList, headers);
//            ResponseEntity<String> roleResponse = restTemplate.postForEntity(roleAssignmentUrl, roleRequest, String.class);
//
//            if (!roleResponse.getStatusCode().is2xxSuccessful()) {
//                logger.error("Failed to assign role to user: {}", roleResponse.getBody());
//                return ResponseEntity.status(roleResponse.getStatusCode())
//                        .body("User created but role assignment failed: " + roleResponse.getBody());
//            }
//
//            logger.info("Role assigned successfully to user");
//
//            // Step 5: Save user in PostgreSQL
//            User user = new User();
//            user.setKeycloakId(userId);
//            user.setUsername(request.getUsername());
//            user.setEmail(request.getEmail());
//            user.setRole(request.getRole().toLowerCase());
//            userRepository.save(user);
//
//            logger.info("User saved successfully in PostgreSQL database");
//
//            return ResponseEntity.ok("User registered and role assigned successfully!");
//
//        } catch (Exception e) {
//            logger.error("Error during user registration process", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Registration failed due to an internal error: " + e.getMessage());
//        }
//
//    }
//
//    // Helper method to extract user ID
//    private String extractUserIdFromResponse(String responseBody) {
//        try {
//            JsonNode userArray = objectMapper.readTree(responseBody);
//            if (userArray.isArray() && userArray.size() > 0) {
//                return userArray.get(0).get("id").asText();
//            }
//        } catch (Exception e) {
//            logger.error("Error extracting user ID from response", e);
//        }
//        return null;
//    }
//
//    // Helper method to find role ID
//    private String findRoleId(String rolesResponseBody, String roleName) {
//        try {
//            JsonNode rolesArray = objectMapper.readTree(rolesResponseBody);
//            for (JsonNode role : rolesArray) {
//                if (role.has("name") && role.get("name").asText().equals(roleName)) {
//                    return role.get("id").asText();
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error finding role ID", e);
//        }
//        return null;
//    }
//}
