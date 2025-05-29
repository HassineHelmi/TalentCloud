package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillsRepository extends JpaRepository<Skills, Long> {

    // Find skills by candidateId
    List<Skills> findByCandidate_Id(Long candidateId);  // Updated query
}
