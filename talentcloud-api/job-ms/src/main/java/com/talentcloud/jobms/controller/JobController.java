package com.talentcloud.jobms.controller;

import com.talentcloud.jobms.dto.CreateJobDTO;
import com.talentcloud.jobms.dto.JobDTO;
import com.talentcloud.jobms.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody CreateJobDTO jobDTO,
                                            @AuthenticationPrincipal Jwt principal) {
        return new ResponseEntity<>(jobService.createJob(jobDTO, principal.getSubject()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id,
                                            @Valid @RequestBody CreateJobDTO jobDTO,
                                            @AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(jobService.updateJob(id, jobDTO, principal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateJob(@PathVariable Long id, @AuthenticationPrincipal Jwt principal) {
        jobService.deactivateJob(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<JobDTO> reactivateJob(@PathVariable Long id, @AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok(jobService.reactivateJob(id, principal));
    }
}