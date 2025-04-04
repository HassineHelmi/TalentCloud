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

    @PreAuthorize("hasAnyRole(Role.ROLE_ADMIN.getRole(), Role.ROLE_CLIENT.getRole())")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    @PreAuthorize("hasRole(Role.ROLE_CANDIDATE.getRole())")
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody UserProfileDto profile) {
        profileService.saveProfile(profile);
        return ResponseEntity.ok("Profile created");
    }
}
