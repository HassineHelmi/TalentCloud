package com.talentcloud.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KeycloakService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    // Update with the same port as in AuthService
    private final String KEYCLOAK_BASE_URL = "http://172.20.52.160:30080";
    private final String KEYCLOAK_TOKEN_URL = KEYCLOAK_BASE_URL + "/realms/master/protocol/openid-connect/token";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KeycloakService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getAdminToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", "admin-cli");
            body.add("username", "admin");  // Your Keycloak admin username
            body.add("password", "admin");  // Your Keycloak admin password

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_TOKEN_URL, request, String.class);

            JsonNode tokenJson = objectMapper.readTree(response.getBody());
            return tokenJson.get("access_token").asText();
        } catch (Exception e) {
            logger.error("Failed to get admin token from Keycloak", e);
            throw new RuntimeException("Failed to get admin token", e);
        }
    }
}