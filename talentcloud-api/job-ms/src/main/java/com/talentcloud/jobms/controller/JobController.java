package com.talentcloud.jobms.controller;

import com.talentcloud.jobms.dto.CreateJobDTO;

import com.talentcloud.jobms.dto.JobDTO;
import com.talentcloud.jobms.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody CreateJobDTO jobDTO) {
        return new ResponseEntity<>(jobService.createJob(jobDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @Valid @RequestBody CreateJobDTO jobDTO) {
        return ResponseEntity.ok(jobService.updateJob(id, jobDTO));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateJob(@PathVariable Long id) {
        jobService.deactivateJob(id);
        return ResponseEntity.noContent().build();
    }

}