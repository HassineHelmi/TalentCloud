//package com.talentcloud.profile.service;
//
//import com.talentcloud.profile.dto.CvParsedDataDto;
//import com.talentcloud.profile.dto.UpdateProfileDto;
//import com.talentcloud.profile.dto.UserProfileDto;
//import com.talentcloud.profile.iservice.IServiceProfile;
//import com.talentcloud.profile.model.Profile;
//import com.talentcloud.profile.repository.AdminRepository;
//import com.talentcloud.profile.repository.CandidateRepository;
//import com.talentcloud.profile.repository.ClientRepository;
//import com.talentcloud.profile.repository.ProfileRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class ProfileService implements IServiceProfile {
//
//    private final ProfileRepository profileRepository;
//    private final CandidateRepository candidateRepository;
//    private final ClientRepository clientRepository;
//    private final AdminRepository adminRepository;
//    private final S3Service s3Service;
//    private final PythonParsingClient pythonParsingClient;
////    private final CvProcessingService cvProcessingService;
//
//    public ProfileService(ProfileRepository profileRepository, CandidateRepository candidateRepository, ClientRepository clientRepository, AdminRepository adminRepository, S3Service s3Service, PythonParsingClient pythonParsingClient, CvProcessingService cvProcessingService) {
//        this.profileRepository = profileRepository;
//        this.candidateRepository = candidateRepository;
//        this.clientRepository = clientRepository;
//        this.adminRepository = adminRepository;
//        this.s3Service = s3Service;
//        this.pythonParsingClient = pythonParsingClient;
////        this.cvProcessingService = cvProcessingService;
//    }
//
//    @Override
//    @Transactional
//    public Profile findOrCreateProfile(String authServiceUserId, String email, String firstName, String lastName) {
//        return profileRepository.findByAuthServiceUserId(authServiceUserId).orElseGet(() -> {
//            Profile newProfile = new Profile();
//            newProfile.setAuthServiceUserId(authServiceUserId);
//            newProfile.setEmail(email);
//            newProfile.setFirstName(firstName != null ? firstName : "");
//            newProfile.setLastName(lastName != null ? lastName : "");
//            return profileRepository.save(newProfile);
//        });
//    }
//
//    @Override
//    @Transactional
//    public Profile updateProfile(String authServiceUserId, UpdateProfileDto profileDto) {
//        Profile profile = profileRepository.findByAuthServiceUserId(authServiceUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user"));
//
//        profile.setFirstName(profileDto.getFirstName());
//        profile.setLastName(profileDto.getLastName());
//        profile.setAddress(profileDto.getAddress());
//        profile.setPhoneNumber(profileDto.getPhoneNumber());
//
//        return profileRepository.save(profile);
//    }
//
//    @Override
//    @Transactional
//    public UserProfileDto getFullUserProfile(Jwt jwt) {
//        String jwtSub = jwt.getClaimAsString("sub");
//        String email = jwt.getClaimAsString("email");
//        String firstName = jwt.getClaimAsString("given_name");
//        String lastName = jwt.getClaimAsString("family_name");
//        Profile baseProfile = this.findOrCreateProfile(jwtSub, email, firstName, lastName);
//
//        UserProfileDto userProfileDto = new UserProfileDto();
//        userProfileDto.setProfile(baseProfile);
//
//        @SuppressWarnings("unchecked")
//        List<String> roles = ((List<String>) jwt.getClaimAsMap("realm_access").get("roles"));
//        userProfileDto.setRoles(roles);
//
//        if (roles.contains("ROLE_CANDIDATE")) {
//            candidateRepository.findByProfileUserId(baseProfile.getId())
//                    .ifPresent(userProfileDto::setCandidateDetails);
//        }
//        if (roles.contains("ROLE_CLIENT")) {
//            clientRepository.findByProfileUserId(baseProfile.getId())
//                    .ifPresent(userProfileDto::setClientDetails);
//        }
//        if (roles.contains("ROLE_ADMIN")) {
//            adminRepository.findByProfileUserId(baseProfile.getId())
//                    .ifPresent(userProfileDto::setAdminDetails);
//        }
//        return userProfileDto;
//    }
//
//    @Async
//    public void processCvUploadAsync(MultipartFile file, String bucketName, String s3Key, String authUserId) {
//        try {
//            s3Service.uploadFile(bucketName, s3Key, file);
//            System.out.println("Calling Python parsing service for key: " + s3Key);
//            CvParsedDataDto parsedData = pythonParsingClient.parseCvFromS3(s3Key);
//
//            if (parsedData != null) {
//                System.out.println("Received parsed data. Hydrating profile for user: " + authUserId);
//                cvProcessingService.hydrateProfileFromCv(authUserId, s3Key, parsedData);
//                System.out.println("Profile hydration complete for user: " + authUserId);
//            } else {
//                System.err.println("CV Parsing returned no data for user: " + authUserId);
//            }
//        } catch (IOException e) {
//            System.err.println("Failed to upload file to S3 for user " + authUserId + ". Error: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred during async CV processing for user " + authUserId + ". Error: " + e.getMessage());
//        }
//    }
//}