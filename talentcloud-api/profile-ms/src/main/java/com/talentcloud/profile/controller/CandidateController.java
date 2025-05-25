package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.dto.ErrorResponse;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.iservice.IServiceCandidate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    @Autowired
    public CandidateController(IServiceCandidate candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> getMyCandidateProfile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        Optional<Candidate> candidate = candidateService.getCandidateProfileByUserId(userId);
        if (candidate.isPresent()) {
            return ResponseEntity.ok(candidate.get());
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Candidate profile not found for user ID " + userId,
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Candidate> createCandidateProfile(@RequestBody @Valid Candidate candidate, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        if (!userId.equals(candidate.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Candidate savedCandidate = candidateService.createCandidateProfile(candidate);
        return ResponseEntity.ok(savedCandidate);
    }

    @PutMapping("/{candidateId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockCandidateProfile(@PathVariable Long candidateId) {
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
    public ResponseEntity<?> getCandidateById(@PathVariable Long candidateId, Authentication authentication) {
        Optional<Candidate> optionalCandidate = candidateService.getCandidateById(candidateId);

        if (optionalCandidate.isPresent()) {
            Candidate candidate = optionalCandidate.get();
            Long currentUserId = Long.valueOf(authentication.getName());
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            if (candidate.getUserId().equals(currentUserId)) {
                return ResponseEntity.ok(candidate);
            } else if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_CLIENT")) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> editCandidateProfile(
            Authentication authentication,
            @RequestBody @Valid UpdateCandidateDto dto
    ) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            Optional<Candidate> optionalCandidate = candidateService.getCandidateProfileByUserId(userId);
            if (optionalCandidate.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate profile not found for authenticated user.");
            }
            Long candidateId = optionalCandidate.get().getCandidateId();

            Candidate updatedCandidate = candidateService.editCandidateProfile(candidateId, dto);
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
