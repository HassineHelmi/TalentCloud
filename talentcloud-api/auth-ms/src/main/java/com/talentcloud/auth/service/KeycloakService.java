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
import org.springframework.beans.factory.annotation.Value;

@Service
public class KeycloakService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    
    @Value("${KEYCLOAK_URL}")
    private String keycloakBaseUrl;
    @Value("${KEYCLOAK_ADMIN_USERNAME}")
    private String adminUsername;
    @Value("${KEYCLOAK_ADMIN_PASSWORD}")
    private String adminPassword;

    private String getAdminTokenUrl() {
        return keycloakBaseUrl + "/realms/master/protocol/openid-connect/token";
    }

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
            body.add("username", adminUsername);
            body.add("password", adminPassword);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(getAdminTokenUrl(), request, String.class);

            JsonNode tokenJson = objectMapper.readTree(response.getBody());
            return tokenJson.get("access_token").asText();
        } catch (Exception e) {
            logger.error("Failed to get admin token from Keycloak", e);
            throw new RuntimeException("Failed to get admin token", e);
        }
    }
}
