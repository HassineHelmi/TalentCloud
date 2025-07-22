package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.CreateCandidateDto;
import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.service.LambdaService;
import com.talentcloud.profile.service.ProfileService;
import com.talentcloud.profile.service.S3Service;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);
    
    private final IServiceCandidate candidateService;
    private final ProfileService profileService;
    private final S3Service s3Service;
    private final LambdaService lambdaService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public CandidateController(IServiceCandidate candidateService, ProfileService profileService, 
                              S3Service s3Service, LambdaService lambdaService) {
        this.candidateService = candidateService;
        this.profileService = profileService;
        this.s3Service = s3Service;
        this.lambdaService = lambdaService;
    }

    @PostMapping("/me/upload-cv")
    public ResponseEntity<String> uploadMyCv(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Cannot upload an empty file.");
        }
        
        String authUserId = jwt.getClaimAsString("sub");
        String uniqueFileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        String s3Key = String.format("resumes/%s", uniqueFileName);

        try {
            // 1. Upload file to S3
            logger.info("Uploading CV to S3 for user {}", authUserId);
            s3Service.uploadFile(bucketName, s3Key, file);
            
            // 2. Trigger Lambda function to parse the CV
            logger.info("Triggering Lambda function for CV parsing");
            lambdaService.triggerCvParsing(s3Key, authUserId, bucketName);
            
            return ResponseEntity.ok("CV uploaded successfully and processing started. You will receive a notification when complete.");
        } catch (Exception e) {
            logger.error("Failed to process CV upload", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process CV upload: " + e.getMessage(), e);
        }
    }

    @PostMapping("/me")
    public ResponseEntity<?> createMyCandidateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateCandidateDto dto) {
        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"), jwt.getClaimAsString("family_name"));

        if (candidateService.getCandidateProfileByProfileUserId(userProfile.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Candidate profile already exists for this user.");
        }

        Candidate candidate = new Candidate();
        candidate.setProfileUserId(userProfile.getId());
        candidate.setResumeUrl(dto.getResumeUrl());
        candidate.setJobTitle(dto.getJobTitle());
        candidate.setJobCategory(dto.getJobCategory());
        candidate.setVisibilitySettings(dto.getVisibilitySettings());

        Candidate createdCandidate = candidateService.createCandidateProfile(candidate);
        return new ResponseEntity<>(createdCandidate, HttpStatus.CREATED);
    }

    @PutMapping("/me")
    public ResponseEntity<Candidate> editMyCandidateDetails(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpdateCandidateDto dto) {
        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"), jwt.getClaimAsString("family_name"));

        Candidate candidate = candidateService.getCandidateProfileByProfileUserId(userProfile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate profile not found for authenticated user."));

        Candidate updatedCandidate = candidateService.editCandidateProfile(candidate.getId(), dto);
        return ResponseEntity.ok(updatedCandidate);
    }
}