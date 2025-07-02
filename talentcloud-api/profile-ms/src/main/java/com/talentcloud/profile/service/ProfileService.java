package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CvDataDto;
import com.talentcloud.profile.dto.UpdateProfileDto;
import com.talentcloud.profile.dto.UserProfileDto;
import com.talentcloud.profile.iservice.IServiceProfile;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.AdminRepository;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.ClientRepository;
import com.talentcloud.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfileService implements IServiceProfile {

    private final ProfileRepository profileRepository;
    private final CandidateRepository candidateRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, CandidateRepository candidateRepository, ClientRepository clientRepository, AdminRepository adminRepository) {
        this.profileRepository = profileRepository;
        this.candidateRepository = candidateRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public Profile findOrCreateProfile(String authServiceUserId, String email, String firstName, String lastName) {
        return profileRepository.findByAuthServiceUserId(authServiceUserId).orElseGet(() -> {
            Profile newProfile = new Profile();
            newProfile.setAuthServiceUserId(authServiceUserId);
            newProfile.setEmail(email);
            newProfile.setFirstName(firstName != null ? firstName : "");
            newProfile.setLastName(lastName != null ? lastName : "");
            return profileRepository.save(newProfile);
        });
    }

    @Override
    @Transactional
    public Profile updateProfile(String authServiceUserId, UpdateProfileDto profileDto) {
        Profile profile = profileRepository.findByAuthServiceUserId(authServiceUserId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user"));

        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setAddress(profileDto.getAddress());
        profile.setPhoneNumber(profileDto.getPhoneNumber());

        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public UserProfileDto getFullUserProfile(Jwt jwt) {
        // Step 1: Get the base profile
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        Profile baseProfile = this.findOrCreateProfile(jwtSub, email, firstName, lastName);

        // Step 2: Prepare the DTO
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setProfile(baseProfile);

        // Step 3: Extract roles and fetch role-specific data
        @SuppressWarnings("unchecked")
        List<String> roles = ((List<String>) jwt.getClaimAsMap("realm_access").get("roles"));
        userProfileDto.setRoles(roles);

        if (roles.contains("ROLE_CANDIDATE")) {
            candidateRepository.findByProfileUserId(baseProfile.getId())
                    .ifPresent(userProfileDto::setCandidateDetails);
        }
        if (roles.contains("ROLE_CLIENT")) {
            clientRepository.findByProfileUserId(baseProfile.getId())
                    .ifPresent(userProfileDto::setClientDetails);
        }
        if (roles.contains("ROLE_ADMIN")) {
            adminRepository.findByProfileUserId(baseProfile.getId())
                    .ifPresent(userProfileDto::setAdminDetails);
        }

        return userProfileDto;
    }

    @Async
    public void intiitaeCvParsing (String s3FileKey, Long profileId ) {
        String pythonServiceUrl = "http://10.255.255.254:8008/parse-cv-from-s3";
        System.out.println("Calling Python service at: " + pythonServiceUrl);


        try {
            RestTemplate restTemplate = new RestTemplate();

            // Create the JSON request body: {"s3_file_key": "your_key"}
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("s3_file_key", s3FileKey);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody);

            // Make the POST request and expect a CvDataDto in response
            CvDataDto parsedData = restTemplate.postForObject(pythonServiceUrl, request, CvDataDto.class);

            if (parsedData != null) {
                System.out.println("Successfully received data for: " + parsedData.getFirstName());

                // TODO: Here you will use your Mapper to convert `parsedData` (the DTO)
                // into your `CandidateProfile` JPA entity.

                // TODO: Save the partially filled CandidateProfile to your database.

                // TODO: Notify the user that the profile is ready for review.
            }
        } catch (Exception e) {
            // It's crucial to handle errors here!
            System.err.println("Error calling Python CV parsing service: " + e.getMessage());
            // TODO: Handle the failure case (e.g., update profile status to FAILED)
        }

    }



}