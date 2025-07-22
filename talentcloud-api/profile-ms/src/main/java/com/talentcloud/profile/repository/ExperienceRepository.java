package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByCandidateId(Long candidateId);

    @Modifying
    @Query("DELETE FROM Experience e WHERE e.id = :experienceId")
    void deleteExperienceById(@Param("experienceId") Long experienceId);

    @Modifying
    @Query("DELETE FROM Experience e WHERE e.candidate.id = :candidateId")
    void deleteByCandidateId(@Param("candidateId") Long candidateId);
}
