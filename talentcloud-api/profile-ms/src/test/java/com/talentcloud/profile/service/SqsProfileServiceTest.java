//package com.talentcloud.profile.service;
//
//import com.talentcloud.profile.dto.CvParsedDataDto;
//import com.talentcloud.profile.model.Profile;
//import com.talentcloud.profile.repository.ProfileRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//    @Test
//    void testProcessProfileData_ExistingProfile() {
//        // Setup
//        CvParsedDataDto parsedData = new CvParsedDataDto();
//        parsedData.setEmail("test@example.com");
//        parsedData.setFirstName("Test");
//        parsedData.setLastName("User");
//
//        Profile existingProfile = new Profile();
//        existingProfile.setId(1L);
//        existingProfile.setAuthServiceUserId("auth-123");
//        existingProfile.setEmail("test@example.com");
//
//        when(profileRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingProfile));
//
//        // Execute
//        sqsProfileService.processProfileData(parsedData);
//
//        // Verify
//        verify(cvProcessingService).hydrateProfileFromCv(
//                existingProfile.getAuthServiceUserId(),
//                anyString(),
//                any(CvParsedDataDto.class));
//    }
//
//    @Test
//    void testProcessProfileData_NewProfile() {
//        // Setup
//        CvParsedDataDto parsedData = new CvParsedDataDto();
//        parsedData.setEmail("new@example.com");
//        parsedData.setFirstName("New");
//        parsedData.setLastName("User");
//
//        Profile newProfile = new Profile();
//        newProfile.setId(2L);
//        newProfile.setAuthServiceUserId("sqs-generated-id");
//        newProfile.setEmail("new@example.com");
//
//        when(profileRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
//        when(profileRepository.save(any(Profile.class))).thenReturn(newProfile);
//
//        // Execute
//        sqsProfileService.processProfileData(parsedData);
//
//        // Verify
//        verify(profileRepository).save(any(Profile.class));
//        verify(cvProcessingService).hydrateProfileFromCv(
//                anyString(),
//                anyString(),
//                any(CvParsedDataDto.class));
//    }
//}