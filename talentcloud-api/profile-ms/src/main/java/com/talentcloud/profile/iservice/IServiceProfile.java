package com.talentcloud.profile.iservice;

import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.ProfileRepository;
import com.talentcloud.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IServiceProfile implements ProfileService {

    private ProfileRepository profileRepository;

    @Autowired
    public IServiceProfile(ProfileRepository profileRepository) {
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