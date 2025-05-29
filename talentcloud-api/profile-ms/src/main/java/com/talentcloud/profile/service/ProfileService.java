package com.talentcloud.profile.service;
import com.talentcloud.profile.model.Profile;

public interface ProfileService {
    Profile findOrCreateProfile(String authServiceUserId, String email);
}
