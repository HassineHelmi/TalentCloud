package com.talentcloud.profile.service;
import com.talentcloud.profile.dto.UpdateEducationDto;

import com.talentcloud.profile.iservice.IServiceEducation;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Education;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.EducationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EducationService implements IServiceEducation {

    private final EducationRepository educationRepository;
    private final CandidateRepository candidateRepository;

    public EducationService(EducationRepository educationRepository, CandidateRepository candidateRepository) {
        this.educationRepository = educationRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Education addEducation(Education education, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));

        education.setCandidate(candidate);
        education.setCreatedAt(LocalDateTime.now());

        return educationRepository.save(education);
    }

    @Override
    public Education deleteEducation(Long educationId) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found with ID: " + educationId));

        educationRepository.delete(existingEducation); // Delete the education record
        return existingEducation; // Return the deleted education for confirmation
    }

    @Override
    public Education editEducation(Long educationId, UpdateEducationDto dto) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found with ID: " + educationId));

        if (dto.getInstitution() != null) existingEducation.setInstitution(dto.getInstitution());
        if (dto.getDiplome() != null) existingEducation.setDiplome(dto.getDiplome());
        if (dto.getDomaineEtude() != null) existingEducation.setDomaineEtude(dto.getDomaineEtude());
        if (dto.getDateDebut() != null) existingEducation.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) existingEducation.setDateFin(dto.getDateFin());
        if (dto.getMoyenne() != null) existingEducation.setMoyenne(dto.getMoyenne());
        if (dto.getEnCours() != null) existingEducation.setEnCours(dto.getEnCours());

        existingEducation.setUpdatedAt(LocalDateTime.now());

        return educationRepository.save(existingEducation); // Save the updated education record
    }


}
