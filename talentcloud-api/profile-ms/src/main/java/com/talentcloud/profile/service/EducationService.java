package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateEducationDto;
import com.talentcloud.profile.iservice.IServiceEducation;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Education;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.EducationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Education addEducation(Education education, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with ID: " + candidateId));
        education.setCandidate(candidate);
        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public Education editEducation(Long educationId, UpdateEducationDto dto) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with ID: " + educationId));

        if (dto.getInstitutionName() != null) existingEducation.setInstitutionName(dto.getInstitutionName());
        if (dto.getDegree() != null) existingEducation.setDegree(dto.getDegree());
        if (dto.getFieldOfStudy() != null) existingEducation.setFieldOfStudy(dto.getFieldOfStudy());
        if (dto.getStartDate() != null) existingEducation.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existingEducation.setEndDate(dto.getEndDate());
        if (dto.getIsCurrent() != null) existingEducation.setIsCurrent(dto.getIsCurrent());

        return educationRepository.save(existingEducation);
    }

    @Override
    @Transactional
    public void deleteEducation(Long educationId) {
        if (!educationRepository.existsById(educationId)) {
            throw new EntityNotFoundException("Education not found with ID: " + educationId);
        }
        educationRepository.deleteEducationById(educationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> getAllEducationByCandidateId(Long candidateId) {
        return educationRepository.findByCandidateId(candidateId);
    }
}
