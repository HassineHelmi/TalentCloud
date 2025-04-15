package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateExperienceDto;
import com.talentcloud.profile.model.Experience;
import com.talentcloud.profile.iservice.IServiceExperience;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiences")
public class ExperienceController {

    private final IServiceExperience experienceService;

    public ExperienceController(IServiceExperience experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping("/create/{candidateId}")
    public ResponseEntity<Experience> createExperience(@PathVariable Long candidateId, @RequestBody @Valid Experience experience) {
        Experience saved = experienceService.createExperience(experience, candidateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{experienceId}/update")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long experienceId, @RequestBody UpdateExperienceDto dto) {
        Experience updated = experienceService.updateExperience(experienceId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{experienceId}/delete")
    public ResponseEntity<Experience> deleteExperience(@PathVariable Long experienceId) {
        Experience deleted = experienceService.deleteExperience(experienceId);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<Experience> getExperienceById(@PathVariable Long experienceId) {
        return experienceService.getExperienceById(experienceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Experience>> getAllExperiencesByCandidate(@PathVariable Long candidateId) {
        List<Experience> experiences = experienceService.getAllExperiencesByCandidateId(candidateId);
        return ResponseEntity.ok(experiences);
    }
}
