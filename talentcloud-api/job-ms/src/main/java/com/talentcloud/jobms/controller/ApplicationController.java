package com.talentcloud.jobms.controller;

import com.talentcloud.jobms.dto.ApplicationDTO;
import com.talentcloud.jobms.dto.CreateApplicationDTO;
import com.talentcloud.jobms.model.ApplicationStatus;
import com.talentcloud.jobms.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<ApplicationDTO> applyForJob(@Valid @RequestBody CreateApplicationDTO dto) {
        return new ResponseEntity<>(applicationService.submitApplication(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<ApplicationDTO> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getApplicationsForCandidate(candidateId));
    }
}