package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.dto.ErrorResponse;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Profile; // Import Profile model
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.service.ProfileService; // Import ProfileService interface

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateController {

    private final IServiceCandidate candidateService;
    private final ProfileService profileService; // Inject ProfileService

    @Autowired
    public CandidateController(IServiceCandidate candidateService, ProfileService profileService) {
        this.candidateService = candidateService;
        this.profileService = profileService; // Initialize ProfileService
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    public ResponseEntity<?> getMyCandidateProfile(@AuthenticationPrincipal Jwt jwt) {
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");

        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email);
        Optional<Candidate> candidate = candidateService.getCandidateProfileByProfileUserId(userProfile.getId());

        if (candidate.isPresent()) {
            return ResponseEntity.ok(candidate.get());
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Candidate-specific details not found for user " + jwtSub + ". Profile may exist but not as a candidate.",
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> createCandidateProfile(
            @RequestBody @Valid UpdateCandidateDto candidateDto,
            @AuthenticationPrincipal Jwt jwt) {

        String userIdFromJwtSub = jwt.getClaimAsString("sub");
        String emailFromJwt = jwt.getClaimAsString("email");

        // Step 1: Find or create the generic profile in 'profiles' table
        Profile genericProfile = profileService.findOrCreateProfile(userIdFromJwtSub, emailFromJwt);
        Long profileTableId = genericProfile.getId(); // This is the Long ID

        // Optional: Check if a candidate profile already exists for this profile_id
        if (candidateService.getCandidateProfileByProfileUserId(profileTableId).isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Candidate profile already exists for this user.",
                    "Conflict", // HTTP 409 Conflict
                    LocalDateTime.now(),
                    HttpStatus.CONFLICT.value()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        Candidate candidate = new Candidate();

        // Step 2: Set the profileUserId with the Long ID from the 'profiles' table
        candidate.setProfileUserId(profileTableId); // Correctly setting the Long ID

        // Step 3: Map DTO fields
        candidate.setResume_url(candidateDto.getResume_url());
        candidate.setJobPreference(candidateDto.getJobPreference());

        // Corrected Enum handling for visibilitySetting
        // This assumes candidateDto.getVisibilitySettings() returns the VisibilitySettings enum directly
        if (candidateDto.getVisibilitySettings() != null) {
            candidate.setVisibilitySetting(candidateDto.getVisibilitySettings());
        } else {
            // Set a default visibility if null and required, or handle as an error
            // For example, if VisibilitySettings has a DEFAULT or PRIVATE member:
            // candidate.setVisibilitySetting(VisibilitySettings.PRIVATE);
            // Or, if it's mandatory, you might want to return a BadRequest error if it's null,
            // depending on your @Valid annotations on UpdateCandidateDto for this field.
        }


        try {
            Candidate savedCandidate = candidateService.createCandidateProfile(candidate);
            return ResponseEntity.ok(savedCandidate);
        } catch (DataIntegrityViolationException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Failed to save candidate profile due to data integrity issues: " + e.getMessage(),
                    "Bad Request",
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "An unexpected error occurred while saving the profile: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{candidateId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockCandidateProfile(@PathVariable Long candidateId) {
        // candidateId here is the PK of the 'candidates' table (candidates.id)
        try {
            Candidate blockedCandidate = candidateService.blockProfile(candidateId);
            return ResponseEntity.ok(blockedCandidate);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Error blocking candidate: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{candidateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'CANDIDATE')")
    public ResponseEntity<?> getCandidateById(@PathVariable Long candidateId, @AuthenticationPrincipal Jwt jwt) {
        // candidateId here is the PK of the 'candidates' table (candidates.id)
        Optional<Candidate> optionalCandidate = candidateService.getCandidateById(candidateId);

        if (optionalCandidate.isPresent()) {
            Candidate candidate = optionalCandidate.get();
            String currentJwtSub = jwt.getClaimAsString("sub");
            String currentEmail = jwt.getClaimAsString("email");

            // Determine if the current authenticated user is the owner of this candidate profile
            Profile requestUserProfile = profileService.findOrCreateProfile(currentJwtSub, currentEmail);
            boolean isOwner = requestUserProfile.getId().equals(candidate.getProfileUserId());

            // Get roles from JWT
            @SuppressWarnings("unchecked")
            List<String> realmRoles = jwt.getClaimAsMap("realm_access") != null ?
                    (List<String>) jwt.getClaimAsMap("realm_access").get("roles") :
                    List.of();
            Set<String> roles = realmRoles.stream().collect(Collectors.toSet());


            if (isOwner || roles.contains("ROLE_ADMIN") || roles.contains("ROLE_CLIENT")) {
                return ResponseEntity.ok(candidate);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to this candidate profile.");
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Candidate not found with id " + candidateId,
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    public ResponseEntity<?> editCandidateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateCandidateDto dto
    ) {
        try {
            String jwtSub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            Profile userProfile = profileService.findOrCreateProfile(jwtSub, email);

            Optional<Candidate> optionalCandidate = candidateService.getCandidateProfileByProfileUserId(userProfile.getId());

            if (optionalCandidate.isEmpty()) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "Candidate profile not found for authenticated user.",
                        "Not Found",
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Long candidatePrimaryKey = optionalCandidate.get().getId();
            Candidate updatedCandidate = candidateService.editCandidateProfile(candidatePrimaryKey, dto);
            return ResponseEntity.ok(updatedCandidate);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Error updating candidate profile: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
