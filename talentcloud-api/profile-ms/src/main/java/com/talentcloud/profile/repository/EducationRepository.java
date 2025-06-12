package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EducationRepository extends JpaRepository<Education, Long> {
        List<Education> findByCandidate_id(Long candidateId);
}
