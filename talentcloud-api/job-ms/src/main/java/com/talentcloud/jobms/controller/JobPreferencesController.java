package com.talentcloud.jobms.controller;


import com.talentcloud.jobms.dto.JobPreferencesDTO;
import com.talentcloud.jobms.service.JobPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
public class JobPreferencesController {

    private final JobPreferencesService preferencesService;

    @PutMapping
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<JobPreferencesDTO> savePreferences(@Valid @RequestBody JobPreferencesDTO dto) {
        return ResponseEntity.ok(preferencesService.saveOrUpdatePreferences(dto));
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<JobPreferencesDTO> getPreferences(@PathVariable Long candidateId) {
        return ResponseEntity.ok(preferencesService.getPreferences(candidateId));
    }
}