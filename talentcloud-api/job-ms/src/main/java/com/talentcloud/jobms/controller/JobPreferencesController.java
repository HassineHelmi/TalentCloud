package com.talentcloud.jobms.controller;


import com.talentcloud.jobms.dto.CreateJobPreferencesDTO;
import com.talentcloud.jobms.dto.JobPreferencesDTO;
import com.talentcloud.jobms.service.JobPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
public class JobPreferencesController {

    private final JobPreferencesService preferencesService;

    @PutMapping
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<JobPreferencesDTO> savePreferences(@Valid @RequestBody CreateJobPreferencesDTO dto,
                                                             @AuthenticationPrincipal Jwt principal) {
        String candidateAuthId = principal.getSubject();
        return ResponseEntity.ok(preferencesService.saveOrUpdatePreferences(dto, candidateAuthId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<JobPreferencesDTO> getMyPreferences(@AuthenticationPrincipal Jwt principal) {
        String candidateAuthId = principal.getSubject();
        return ResponseEntity.ok(preferencesService.getPreferences(candidateAuthId));
    }
}