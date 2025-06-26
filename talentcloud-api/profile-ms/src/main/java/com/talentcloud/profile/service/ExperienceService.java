package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateExperienceDto;
import com.talentcloud.profile.iservice.IServiceExperience;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Experience;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.ExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExperienceService implements IServiceExperience {

    private final ExperienceRepository experienceRepository;
    private final CandidateRepository candidateRepository;

    public ExperienceService(ExperienceRepository experienceRepository, CandidateRepository candidateRepository) {
        this.experienceRepository = experienceRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    @Transactional
    public Experience createExperience(Experience experience, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + candidateId));
        experience.setCandidate(candidate);
        return experienceRepository.save(experience);
    }

    @Override
    @Transactional
    public Experience updateExperience(Long experienceId, UpdateExperienceDto dto) {
        Experience existing = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new EntityNotFoundException("Experience not found with id: " + experienceId));

        if (dto.getJobTitle() != null) existing.setJobTitle(dto.getJobTitle());
        if (dto.getCompanyName() != null) existing.setCompanyName(dto.getCompanyName());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
        if (dto.getIsCurrent() != null) existing.setIsCurrent(dto.getIsCurrent());
        if (dto.getContractType() != null) existing.setContractType(dto.getContractType());
        if (dto.getTechnologies() != null) existing.setTechnologies(dto.getTechnologies());

        return experienceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteExperience(Long experienceId) {
        if (!experienceRepository.existsById(experienceId)) {
            throw new EntityNotFoundException("Experience not found with id: " + experienceId);
        }
        experienceRepository.deleteExperienceById(experienceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Experience> getExperienceById(Long experienceId) {
        return experienceRepository.findById(experienceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> getAllExperiencesByCandidateId(Long candidateId) {
        return experienceRepository.findByCandidateId(candidateId);
    }
}
