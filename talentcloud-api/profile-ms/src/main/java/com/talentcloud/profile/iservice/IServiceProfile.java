package com.talentcloud.profile.iservice;
import com.talentcloud.profile.model.Profile;

public interface IServiceProfile {
    Profile findOrCreateProfile(String authServiceUserId, String email);
}
