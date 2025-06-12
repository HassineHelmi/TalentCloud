package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateProfileDto;
import com.talentcloud.profile.dto.UserProfileDto;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getMyUnifiedProfile(@AuthenticationPrincipal Jwt jwt) {
        UserProfileDto fullProfile = profileService.getFullUserProfile(jwt);
        return ResponseEntity.ok(fullProfile);
    }


    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileDto profileDto) {

        String jwtSub = jwt.getClaimAsString("sub");
        Profile updatedProfile = profileService.updateProfile(jwtSub, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
