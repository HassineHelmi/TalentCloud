package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CvParsedDataDto;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class SqsProfileService {  

    private final ProfileRepository profileRepository;
    private final CvProcessingService cvProcessingService;

    public SqsProfileService(ProfileRepository profileRepository, CvProcessingService cvProcessingService) {
        this.profileRepository = profileRepository;
        this.cvProcessingService = cvProcessingService;
    }


    @Transactional
    public void processProfileData(CvParsedDataDto parsedData) {
        Profile profile = findExistingProfile(parsedData);

        // If no existing profile found, create a new one
        if (profile == null) {
            profile = createNewProfile(parsedData);
        }

        // Use the existing CV processing service to hydrate the profile
        String resumeKey = parsedData.getResumeKey() != null ?
                parsedData.getResumeKey() : "sqs-generated-" + UUID.randomUUID();
        cvProcessingService.hydrateProfileFromCv(profile.getAuthServiceUserId(), resumeKey, parsedData);
    }

    /**
     * Optimized method to find existing profile with single query approach
     */
    private Profile findExistingProfile(CvParsedDataDto parsedData) {
        // Try auth ID first if available (most reliable)
        if (parsedData.getAuthUserId() != null) {
            Optional<Profile> profileByAuthId = profileRepository.findByAuthServiceUserId(parsedData.getAuthUserId());
            if (profileByAuthId.isPresent()) {
                return profileByAuthId.get();
            }
        }

        // Fallback to email if auth ID not found
        if (parsedData.getEmail() != null && !parsedData.getEmail().isEmpty()) {
            Optional<Profile> profileByEmail = profileRepository.findByEmail(parsedData.getEmail());
            if (profileByEmail.isPresent()) {
                return profileByEmail.get();
            }
        }
        
        return null;
    }

    /**
     * Create a new profile with proper validation
     */
    private Profile createNewProfile(CvParsedDataDto parsedData) {
        Profile profile = new Profile();
        profile.setAuthServiceUserId(parsedData.getAuthUserId() != null ?
                parsedData.getAuthUserId() : generateTemporaryAuthId());
        profile.setEmail(parsedData.getEmail());
        profile.setFirstName(parsedData.getFirstName());
        profile.setLastName(parsedData.getLastName());
        return profileRepository.save(profile);
    }
    
    /**
     * Generate a temporary auth ID for profiles created from SQS
     * These can be linked to real auth IDs later when users register
     */
    private String generateTemporaryAuthId() {
        return "sqs-" + UUID.randomUUID().toString();
    }
}