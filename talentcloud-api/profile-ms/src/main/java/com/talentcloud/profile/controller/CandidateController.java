package com.talentcloud.profile.controller;

import com.talentcloud.profile.service.LambdaService;
import com.talentcloud.profile.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @Value("${aws.s3.bucket-name}")
    private String resumeBucketName;

    private final S3Service s3Service;
    private final LambdaService lambdaService;

    private static final String RESUME_PREFIX = "resumes/";

    public CandidateController(S3Service s3Service, LambdaService lambdaService) {
        this.s3Service = s3Service;
        this.lambdaService = lambdaService;
    }

    @PostMapping("/me/upload-cv")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload an empty or unnamed file.");
        }

        try {
            String authUserId = authentication.getName();

            String s3Key = RESUME_PREFIX + authUserId + "/" + file.getOriginalFilename();

            s3Service.uploadFile(resumeBucketName, s3Key, file);
            logger.info("Successfully uploaded resume to S3 for user {}. Key: {}", authUserId, s3Key);

            lambdaService.triggerCvParsing(s3Key, authUserId, resumeBucketName);

            return ResponseEntity.ok("Resume uploaded successfully and is being processed.");

        } catch (IOException e) {
            logger.error("Error uploading resume for user {}: ", authentication.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload resume due to a server error.");
        }
    }
}