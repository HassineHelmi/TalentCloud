package com.talentcloud.interview_ms.controller;

import com.talentcloud.interview_ms.dto.UserProfileDto;
import com.talentcloud.interview_ms.kafka.event.InterviewCreatedEvent;
import com.talentcloud.interview_ms.kafka.producer.InterviewEventProducer;
import com.talentcloud.interview_ms.service.ProfileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final ProfileClient profileClient;
    private final InterviewEventProducer interviewEventProducer;

    /**
     * Get a user profile by ID from profile-ms (using token)
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String id, Authentication auth) {
        String jwt = ((JwtAuthenticationToken) auth).getToken().getTokenValue();
        return ResponseEntity.ok(profileClient.getProfile(id, jwt));
    }

    /**
     * Mock endpoint to trigger an InterviewCreatedEvent (Kafka test)
     */
    @GetMapping("/mock")
    public ResponseEntity<Void> simulateInterviewCreation() {
        InterviewCreatedEvent event = new InterviewCreatedEvent(
                "interview-" + System.currentTimeMillis(),
                "user-123",
                "Software Engineer",
                Instant.now().toString()
        );
        interviewEventProducer.send(event);
        return ResponseEntity.ok().build();
    }
}
