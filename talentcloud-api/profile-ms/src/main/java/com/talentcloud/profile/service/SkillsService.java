package com.talentcloud.profile.service;

import com.talentcloud.profile.iservice.IServiceSkills;
import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.dto.UpdateSkillsDto;
import com.talentcloud.profile.repository.SkillsRepository;
import com.talentcloud.profile.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SkillsService implements IServiceSkills {

    private final SkillsRepository skillsRepository;
    private final CandidateRepository candidateRepository;


    public SkillsService(SkillsRepository skillsRepository, CandidateRepository candidateRepository) {
        this.skillsRepository = skillsRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    @Transactional
    public Optional<Skills> getSkillsByCandidateId(Long candidateId) {
        return skillsRepository.findByCandidate_Id(candidateId)
                .stream()
                .findFirst(); // Assuming you want to return the first skills found for the candidate
    }

    @Override
    @Transactional
    public Skills addSkills(Skills skills, Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(candidate -> {
                    skills.setCandidate(candidate);
                    skills.setCreated_at(LocalDateTime.now());
                    skills.setUpdated_at(LocalDateTime.now()); // Set updated_at on creation as well
                    return skillsRepository.save(skills);
                })
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
    }

    @Override
    @Transactional
    public Skills updateSkills(Long skillsId, UpdateSkillsDto updateSkillsDto) {
        Skills existingSkills = skillsRepository.findById(skillsId)
                .orElseThrow(() -> new IllegalArgumentException("Skills not found with id: " + skillsId));
        // Update the skills with data from the DTO
        existingSkills.setProgrammingLanguages(updateSkillsDto.getProgrammingLanguage()); // Updated field
        existingSkills.setSoftSkills(updateSkillsDto.getSoftSkills());
        existingSkills.setTechnicalSkill(updateSkillsDto.getTechnicalSkill()); // Updated field
        existingSkills.setToolsAndTechnologies(updateSkillsDto.getToolsAndTechnologies());
        existingSkills.setCustomSkills(updateSkillsDto.getCustomSkill()); // Updated field

        // Set the updatedAt timestamp to the current time
        existingSkills.setUpdated_at(LocalDateTime.now());
        // Save and return the updated skills
        return skillsRepository.save(existingSkills);
    }

    @Override
    @Transactional
    public void deleteSkills(Long skillsId) {
        Skills existingSkills = skillsRepository.findById(skillsId)
                .orElseThrow(() -> new IllegalArgumentException("Skills not found with id: " + skillsId));
        skillsRepository.delete(existingSkills);
    }
}