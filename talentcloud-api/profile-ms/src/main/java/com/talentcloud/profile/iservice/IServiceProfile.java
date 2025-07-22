package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateProfileDto;
import com.talentcloud.profile.dto.UserProfileDto;
import com.talentcloud.profile.model.Profile;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public interface IServiceProfile {
    Profile findOrCreateProfile(String authServiceUserId, String email, String firstName, String lastName);
    Profile updateProfile(String authServiceUserId, UpdateProfileDto profileDto);
    UserProfileDto getFullUserProfile(Jwt jwt);
    Optional<Profile> findProfileByEmail(String email);
}