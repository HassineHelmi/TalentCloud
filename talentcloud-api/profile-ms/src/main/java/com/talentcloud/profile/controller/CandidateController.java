package com.talentcloud.profile.controller;


import com.talentcloud.profile.dto.UpdateCandidateDto;

import com.talentcloud.profile.dto.ErrorResponse;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.iservice.IServiceCandidate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final IServiceCandidate candidateService;

    @Autowired
    public CandidateController(IServiceCandidate candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/create")
    public ResponseEntity<Candidate> createCandidateProfile(@RequestBody @Valid Candidate candidate) {
        Candidate savedCandidate = candidateService.createCandidateProfile(candidate);
        return ResponseEntity.ok(savedCandidate);
    }

    @PutMapping("/{candidateId}/block")
    public ResponseEntity<?> blockCandidateProfile(@PathVariable Long candidateId) {
        try {
            Candidate blockedCandidate = candidateService.blockProfile(candidateId);
            return ResponseEntity.ok(blockedCandidate);
        } catch (Exception e) {
            // Error response for when the blocking fails
            ErrorResponse errorResponse = new ErrorResponse(
                    "Error blocking candidate: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get candidate by ID
    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long candidateId) {
        Optional<Candidate> optionalCandidate = candidateService.getCandidateById(candidateId);

        if (optionalCandidate.isPresent()) {
            return ResponseEntity.ok(optionalCandidate.get());
        }else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Candidate not found with id " + candidateId,
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Get all candidates
    @GetMapping("/all")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }


    @PutMapping("/{candidateId}/edit")
    public ResponseEntity<?> editCandidateProfile(
            @PathVariable Long candidateId,
            @RequestBody @Valid UpdateCandidateDto dto
    ) {
        try {
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
