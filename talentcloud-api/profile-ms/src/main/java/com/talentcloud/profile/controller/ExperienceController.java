package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateExperienceDto;
import com.talentcloud.profile.iservice.IServiceExperience;
import com.talentcloud.profile.model.Experience;
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
    public ResponseEntity<Experience> createExperience(@PathVariable("candidateId") Long candidateId, @RequestBody @Valid Experience experience) {
        Experience savedExperience = experienceService.createExperience(experience, candidateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExperience);
    }

    @PutMapping("/{experienceId}/update")
    public ResponseEntity<Experience> updateExperience(@PathVariable("experienceId") Long experienceId, @RequestBody UpdateExperienceDto dto) {
        Experience updatedExperience = experienceService.updateExperience(experienceId, dto);
        return ResponseEntity.ok(updatedExperience);
    }

    @DeleteMapping("/{experienceId}/delete")
    public ResponseEntity<Void> deleteExperience(@PathVariable("experienceId") Long experienceId) {
        experienceService.deleteExperience(experienceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<Experience> getExperienceById(@PathVariable("experienceId") Long experienceId) {
        return experienceService.getExperienceById(experienceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Experience>> getAllExperiencesByCandidate(@PathVariable("candidateId") Long candidateId) {
        List<Experience> experiences = experienceService.getAllExperiencesByCandidateId(candidateId);
        return ResponseEntity.ok(experiences);
    }
}
