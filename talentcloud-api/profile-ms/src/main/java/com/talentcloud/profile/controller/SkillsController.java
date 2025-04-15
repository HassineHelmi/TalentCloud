package com.talentcloud.profile.controller;

import com.talentcloud.profile.iservice.IServiceSkills;
import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.dto.UpdateSkillsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skills")
public class SkillsController {

    private final IServiceSkills skillsService;

    @Autowired
    public SkillsController(IServiceSkills skillsService) {
        this.skillsService = skillsService;
    }

    // GET: Get skills of a specific candidate
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<Skills> getSkillsByCandidateId(@PathVariable Long candidateId) {
        return skillsService.getSkillsByCandidateId(candidateId)
                .map(skills -> ResponseEntity.ok(skills))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Add skills for a candidate
    @PostMapping("/create/{candidateId}")  // Using candidateId in the path
    public ResponseEntity<Skills> addSkills(@PathVariable Long candidateId, @RequestBody @Valid Skills skills) {
        Skills savedSkills = skillsService.addSkills(skills, candidateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkills);
    }

    // PUT: Update skills for a candidate
    @PutMapping("/{skillsId}")
    public ResponseEntity<Skills> updateSkills(@PathVariable Long skillsId, @RequestBody @Valid UpdateSkillsDto dto) {
        Skills updatedSkills = skillsService.updateSkills(skillsId, dto);
        return ResponseEntity.ok(updatedSkills);
    }

    // DELETE: Delete skills for a candidate
    @DeleteMapping("/{skillsId}")
    public ResponseEntity<Void> deleteSkills(@PathVariable Long skillsId) {
        skillsService.deleteSkills(skillsId);
        return ResponseEntity.noContent().build();
    }
}
