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
        experience.setCreatedAt(LocalDateTime.now());
        experience.setUpdatedAt(LocalDateTime.now());

        return experienceRepository.save(experience);
    }

    @Override
    public Experience updateExperience(Long experienceId, UpdateExperienceDto dto) {
        Experience existing = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found with id: " + experienceId));
        if (dto.getJobTitle() != null) existing.setJobTitle(dto.getJobTitle()); // Updated field
        if (dto.getCompanyName() != null) existing.setCompanyName(dto.getCompanyName()); // Updated field
        if (dto.getDateDebut() != null) existing.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) existing.setDateFin(dto.getDateFin());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getLocation() != null) existing.setLocation(dto.getLocation()); // Updated field
        if (dto.getIsCurrent() != null) existing.setIsCurrent(dto.getIsCurrent()); // Updated field
        if (dto.getEntreprise() != null) existing.setEntreprise(dto.getEntreprise()); // Updated field
        if (dto.getTypeContract() != null) existing.setTypeContract(dto.getTypeContract()); // Updated field
        if (dto.getTechnologies() != null) existing.setTechnologies(dto.getTechnologies());

        existing.setUpdatedAt(LocalDateTime.now());

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