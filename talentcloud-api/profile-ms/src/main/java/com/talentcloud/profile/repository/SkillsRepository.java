package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillsRepository extends JpaRepository<Skills, Long> {

    // Find skills by candidateId
    List<Skills> findByCandidate_Id(Long candidateId);  // Updated query

    @Modifying
    @Query("DELETE FROM Skills s WHERE s.candidate.id = :candidateId")
    void deleteByCandidateId(@Param("candidateId") Long candidateId);
}
