package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateExperienceDto;
import com.talentcloud.profile.iservice.IServiceExperience;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Experience;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.ExperienceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Experience createExperience(Experience experience, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));
        experience.setCandidate(candidate);
        experience.setCreated_at(LocalDateTime.now());
        experience.setUpdated_at(LocalDateTime.now());

        return experienceRepository.save(experience);
    }

    @Override
    public Experience updateExperience(Long experienceId, UpdateExperienceDto dto) {
        Experience existing = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found with id: " + experienceId));
        if (dto.getJobTitle() != null) existing.setJobTitle(dto.getJobTitle());
        if (dto.getCompanyName() != null) existing.setCompanyName(dto.getCompanyName());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existing.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation());
        if (dto.getIsCurrent() != null) existing.setIsCurrent(dto.getIsCurrent());
        if (dto.getCompany() != null) existing.setCompanyName(dto.getCompany());
        if (dto.getContractType() != null) existing.setContractType(dto.getContractType());
        if (dto.getTechnologies() != null) existing.setTechnologies(dto.getTechnologies());

        existing.setUpdated_at(LocalDateTime.now());

        return experienceRepository.save(existing);
    }

    @Override
    public Experience deleteExperience(Long experienceId) {
        Experience existing = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found with id: " + experienceId));
        experienceRepository.delete(existing);
        return existing;
    }

    @Override
    public Optional<Experience> getExperienceById(Long experienceId) {
        return experienceRepository.findById(experienceId);
    }

    @Override
    public List<Experience> getAllExperiencesByCandidateId(Long candidateId) {
        return experienceRepository.findByCandidate_Id(candidateId); // Use Candidate's primary key 'id'
    }
}