package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.EducationRequest;
import com.talentcloud.profile.dto.UpdateEducationDto;
import com.talentcloud.profile.iservice.IServiceEducation;
import com.talentcloud.profile.model.Education;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/educations")
public class EducationController {

    private final IServiceEducation educationService;

    public EducationController(IServiceEducation educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/create/{candidateId}")
    public ResponseEntity<Education> addEducation(
            @PathVariable("candidateId") Long candidateId,
            @RequestBody @Valid EducationRequest request) {

        Education education = new Education();
        education.setInstitutionName(request.institutionName());
        education.setDegree(request.degree());
        education.setFieldOfStudy(request.fieldOfStudy());
        education.setStartDate(request.startDate());
        education.setEndDate(request.endDate());
        education.setIsCurrent(request.isCurrent());

        Education savedEducation = educationService.addEducation(education, candidateId);
        return new ResponseEntity<>(savedEducation, HttpStatus.CREATED);
    }

    @PutMapping("/{educationId}/update")
    public ResponseEntity<Education> editEducation(
            @PathVariable("educationId") Long educationId,
            @RequestBody @Valid UpdateEducationDto dto) {
        Education updatedEducation = educationService.editEducation(educationId, dto);
        return ResponseEntity.ok(updatedEducation);
    }

    @DeleteMapping("/{educationId}/delete")
    public ResponseEntity<Void> deleteEducation(@PathVariable("educationId") Long educationId) {
        educationService.deleteEducation(educationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Education>> getEducationsByCandidateId(@PathVariable("candidateId") Long candidateId) {
        List<Education> educations = educationService.getAllEducationByCandidateId(candidateId);
        return ResponseEntity.ok(educations);
    }
}
