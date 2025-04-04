package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UserProfileDto;
import com.talentcloud.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String id) {
        UserProfileDto profile = profileService.getProfile(id);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody UserProfileDto profile) {
        profileService.saveProfile(profile);
        return ResponseEntity.ok("Profile created");
    }
}
