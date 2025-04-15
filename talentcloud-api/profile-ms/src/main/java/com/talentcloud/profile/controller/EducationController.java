package com.talentcloud.profile.controller;


import com.talentcloud.profile.dto.EducationRequest;
import com.talentcloud.profile.dto.UpdateEducationDto;
import com.talentcloud.profile.model.Education;
import com.talentcloud.profile.iservice.IServiceEducation;
import jakarta.validation.Valid;
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
            @PathVariable Long candidateId,
            @RequestBody @Valid EducationRequest request) {

        Education education = new Education();
        education.setInstitution(request.institution());
        education.setDiplome(request.diplome());
        education.setDomaineEtude(request.domaineEtude());
        education.setDateDebut(request.dateDebut());
        education.setDateFin(request.dateFin());
        education.setMoyenne(request.moyenne());
        education.setEnCours(request.enCours());

        Education saved = educationService.addEducation(education, candidateId);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{educationId}/edit")
    public ResponseEntity<Education> editEducation(
            @PathVariable Long educationId,
            @RequestBody UpdateEducationDto dto) {
        Education updatedEducation = educationService.editEducation(educationId, dto);
        return ResponseEntity.ok(updatedEducation);
    }

    @DeleteMapping("/{educationId}/delete")
    public ResponseEntity<Education> deleteEducation(@PathVariable Long educationId) {
        Education deletedEducation = educationService.deleteEducation(educationId);
        return ResponseEntity.ok(deletedEducation);
    }


}
