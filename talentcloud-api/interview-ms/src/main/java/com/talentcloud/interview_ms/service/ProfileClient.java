package com.talentcloud.interview_ms.service;

import com.talentcloud.interview_ms.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ProfileClient {

    private final WebClient webClient;

    @Value("${profile.service.base-url:http://profile-ms:8082}")
    private String profileBaseUrl;

    public UserProfileDto getProfile(String id, String jwt) {
        return webClient.get()
                .uri(profileBaseUrl + "/api/profile/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .block(); // you can make this async if needed
    }
}
