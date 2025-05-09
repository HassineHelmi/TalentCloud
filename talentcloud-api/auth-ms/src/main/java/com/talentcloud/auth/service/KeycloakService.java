package com.talentcloud.auth.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakService {
    private final RestTemplate restTemplate;
    private final String keycloakTokenUrl = "http://localhost:8080/realms/talentcloud/protocol/openid-connect/token";
    private final String clientId = "public-client";
    private final String clientSecret = "mLEpCbgOzDmSLn7UM4IaTCDFAAMMIRbk";
    private final String realmAdminUrl = "http://localhost:8080/admin/realms/talentcloud/users";
    private final String realmRolesUrl = "http://localhost:8080/admin/realms/talentcloud/roles";

    public KeycloakService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(keycloakTokenUrl, request, Map.class);

        return response.getBody().get("access_token").toString();
    }

    public ResponseEntity<String> fetchUsersFromKeycloak() {
        String adminToken = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(realmAdminUrl, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> fetchRolesFromKeycloak() {
        String adminToken = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(realmRolesUrl, HttpMethod.GET, entity, String.class);
    }
}