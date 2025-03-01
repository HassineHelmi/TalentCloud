package com.profile_service.profile_service.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile_service.profile_service.model.User;
import com.profile_service.profile_service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserSyncScheduler {
    private final UserService userService;
    private final KeycloakService keycloakService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public UserSyncScheduler(UserService userService, KeycloakService keycloakService, ObjectMapper objectMapper, UserRepository userRepository) {
        this.userService = userService;
        this.keycloakService = keycloakService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 60000) // Sync every 60 seconds
    public void syncUsersPeriodically() {
        userService.syncUsersFromKeycloak();
    }
    /**
     * Syncs users from Keycloak to PostgreSQL.
     */
    public void syncUsersFromKeycloak() {

        try {
            // logger.info("Starting user sync from Keycloak");
            ResponseEntity<String> response = keycloakService.fetchUsersFromKeycloak();

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode keycloakUsers = objectMapper.readTree(response.getBody());
                int syncCount = 0;

                for (JsonNode keycloakUser : keycloakUsers) {
                    // Ensure we fetch the correct fields
                    String keycloakId = keycloakUser.has("id") ? keycloakUser.get("id").asText() : null;
                    String username = keycloakUser.has("username") ? keycloakUser.get("username").asText() : "unknown";
                    String email = keycloakUser.has("email") ? keycloakUser.get("email").asText() : "";

                    // Skip if no valid Keycloak ID
                    if (keycloakId == null) {
                        //           logger.warn("Skipping user with no Keycloak ID: {}", username);
                        continue;
                    }

                    // Validate email before storing in PostgreSQL
                    if (!email.isEmpty() && !userRepository.existsByEmail(email)) {
                        try {
                            // Use the new constructor with keycloakId
                            User user = new User(keycloakId, email, username, "user");
                            userRepository.save(user);
                            syncCount++;
                        } catch (Exception e) {
                            //             logger.error("Error saving user: {}", username, e);
                        }
                    }
                }

                //logger.info("User sync completed. Synced {} users", syncCount);
            } else {
                //logger.error("Failed to fetch users from Keycloak: {}", response.getBody());
            }
        } catch (Exception e) {
            //logger.error("Error syncing users from Keycloak", e);
        }
    }
}



