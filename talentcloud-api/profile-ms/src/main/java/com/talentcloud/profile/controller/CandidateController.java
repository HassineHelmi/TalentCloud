//package com.talentcloud.profile.controller;
//
//import com.talentcloud.profile.dto.CreateCandidateDto;
//import com.talentcloud.profile.dto.UpdateCandidateDto;
//import com.talentcloud.profile.iservice.IServiceCandidate;
//import com.talentcloud.profile.model.Candidate;
//import com.talentcloud.profile.model.Profile;
//import com.talentcloud.profile.service.ProfileService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/candidate")
//public class CandidateController {
//
//    private final IServiceCandidate candidateService;
//    private final ProfileService profileService;
//
//    @Value("${cloud.aws.s3.bucket-name}")
//    private String bucketName;
//
//    public CandidateController(IServiceCandidate candidateService, ProfileService profileService) {
//        this.candidateService = candidateService;
//        this.profileService = profileService;
//    }
//
//    @PostMapping("/me/upload-cv")
//    @PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
//    public ResponseEntity<String> uploadMyCv(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("Cannot upload an empty file.");
//        }
//
//        String authUserId = jwt.getClaimAsString("sub");
//        String fileExtension = getFileExtension(file.getOriginalFilename());
//        String s3Key = String.format("cvs/%s/%s%s", authUserId, UUID.randomUUID(), fileExtension);
//
//        try {
//            profileService.processCvUploadAsync(file, bucketName, s3Key, authUserId);
//            return ResponseEntity.accepted().body("CV upload accepted. Profile will be updated shortly.");
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initiate CV processing.", e);
//        }
//    }
//
//    private String getFileExtension(String fileName) {
//        if (fileName == null || fileName.lastIndexOf('.') == -1) {
//            return "";
//        }
//        return fileName.substring(fileName.lastIndexOf('.'));
//    }
//
//    @PostMapping("/me")
//    public ResponseEntity<?> createMyCandidateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateCandidateDto dto) {
//        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"), jwt.getClaimAsString("family_name"));
//
//        if (candidateService.getCandidateProfileByProfileUserId(userProfile.getId()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Candidate profile already exists for this user.");
//        }
//
//        Candidate candidate = new Candidate();
//        candidate.setProfileUserId(userProfile.getId());
//        candidate.setResumeUrl(dto.getResumeUrl());
//        candidate.setJobTitle(dto.getJobTitle());
//        candidate.setJobCategory(dto.getJobCategory());
//        candidate.setVisibilitySettings(dto.getVisibilitySettings());
//
//        Candidate createdCandidate = candidateService.createCandidateProfile(candidate);
//        return new ResponseEntity<>(createdCandidate, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/me")
//    public ResponseEntity<Candidate> editMyCandidateDetails(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpdateCandidateDto dto) {
//        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"), jwt.getClaimAsString("family_name"));
//
//        Candidate candidate = candidateService.getCandidateProfileByProfileUserId(userProfile.getId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate profile not found for authenticated user."));
//
//        Candidate updatedCandidate = candidateService.editCandidateProfile(candidate.getId(), dto);
//        return ResponseEntity.ok(updatedCandidate);
//    }
//}