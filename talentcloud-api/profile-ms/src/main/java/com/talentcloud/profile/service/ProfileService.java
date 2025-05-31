package com.talentcloud.profile.service;

import com.talentcloud.profile.iservice.IServiceProfile;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileService implements IServiceProfile {

    private ProfileRepository profileRepository;

    @Autowired
    public void IServiceProfile(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Profile findOrCreateProfile(String authServiceUserId, String email) {
        Optional<Profile> existingProfile = profileRepository.findByAuthServiceUserId(authServiceUserId);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        } else {
            Profile newProfile = new Profile();
            newProfile.setAuthServiceUserId(authServiceUserId);
            newProfile.setEmail(email);
            return profileRepository.save(newProfile);
        }
    }
}