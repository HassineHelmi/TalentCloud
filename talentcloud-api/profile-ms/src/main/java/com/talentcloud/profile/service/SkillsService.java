package com.talentcloud.profile.service;

import com.talentcloud.profile.iservice.IServiceSkills;
import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.dto.UpdateSkillsDto;  // Import the DTO
import com.talentcloud.profile.repository.SkillsRepository;
import com.talentcloud.profile.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SkillsService implements IServiceSkills {

    private final SkillsRepository skillsRepository;
    private final CandidateRepository candidateRepository;

    @Autowired
    public SkillsService(SkillsRepository skillsRepository, CandidateRepository candidateRepository) {
        this.skillsRepository = skillsRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Optional<Skills> getSkillsByCandidateId(Long candidateId) {
        return skillsRepository.findByCandidate_CandidateId(candidateId);
    }

    @Override
    public Skills addSkills(Skills skills, Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(candidate -> {
                    skills.setCandidate(candidate);
                    skills.setCreatedAt(LocalDateTime.now());  // Set the created_at timestamp
                    return skillsRepository.save(skills);
                })
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
    }

    @Override
    public Skills updateSkills(Long skillsId, UpdateSkillsDto updateSkillsDto) {
        Skills existingSkills = skillsRepository.findById(skillsId)
                .orElseThrow(() -> new IllegalArgumentException("Skills not found with id: " + skillsId));

        // Update the skills with data from the DTO
        existingSkills.setProgrammingLanguages(updateSkillsDto.getProgrammingLanguages());
        existingSkills.setSoftSkills(updateSkillsDto.getSoftSkills());
        existingSkills.setTechnicalSkills(updateSkillsDto.getTechnicalSkills());
        existingSkills.setToolsAndTechnologies(updateSkillsDto.getToolsAndTechnologies());
        existingSkills.setCustomSkills(updateSkillsDto.getCustomSkills());

        // Set the updatedAt timestamp to the current time
        existingSkills.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated skills
        return skillsRepository.save(existingSkills);
    }


    @Override
    public void deleteSkills(Long skillsId) {
        Skills existingSkills = skillsRepository.findById(skillsId)
                .orElseThrow(() -> new IllegalArgumentException("Skills not found with id: " + skillsId));

        skillsRepository.delete(existingSkills);
    }
}
