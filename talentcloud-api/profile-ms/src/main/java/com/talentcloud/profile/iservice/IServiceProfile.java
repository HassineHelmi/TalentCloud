package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateProfileDto;
import com.talentcloud.profile.dto.UserProfileDto; // NEW import
import com.talentcloud.profile.model.Profile;
import org.springframework.security.oauth2.jwt.Jwt; // NEW import

public interface IServiceProfile {
    Profile findOrCreateProfile(String authServiceUserId, String email, String firstName, String lastName);
    Profile updateProfile(String authServiceUserId, UpdateProfileDto profileDto);

    // NEW Method for the intelligent endpoint
    UserProfileDto getFullUserProfile(Jwt jwt);
}