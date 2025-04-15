package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillsRepository extends JpaRepository<Skills, Long> {

    // Find skills by candidateId
    Optional<Skills> findByCandidate_CandidateId(Long candidateId);  // Updated query
}
