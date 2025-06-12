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
        education.setCreated_at(LocalDateTime.now());
        education.setUpdated_at(LocalDateTime.now()); // Set updated_at on creation as well

        return educationRepository.save(education);
    }

    @Override
    public Education deleteEducation(Long educationId) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found with ID: " + educationId));
        educationRepository.delete(existingEducation);
        return existingEducation;
    }

    @Override
    public Education editEducation(Long educationId, UpdateEducationDto dto) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found with ID: " + educationId));
        if (dto.getInstitutionName() != null) existingEducation.setInstitutionName(dto.getInstitutionName());
        if (dto.getDegree() != null) existingEducation.setDegree(dto.getDegree());
        if (dto.getFieldOfStudy() != null) existingEducation.setFieldOfStudy(dto.getFieldOfStudy());
        if (dto.getStartDate() != null) existingEducation.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existingEducation.setEndDate(dto.getEndDate());
        if (dto.getIsCurrent() != null) existingEducation.setIsCurrent(dto.getIsCurrent());
        existingEducation.setUpdated_at(LocalDateTime.now());

        return educationRepository.save(existingEducation);
    }


    @Override
    public List<Education> getAllEducationByCandidateId(Long candidateId) {
        return educationRepository.findByCandidate_id(candidateId);
    }


}