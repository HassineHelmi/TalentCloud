package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

        List<Education> findByCandidateId(Long candidateId);

        @Modifying
        @Query("DELETE FROM Education e WHERE e.id = :educationId")
        void deleteEducationById(@Param("educationId") Long educationId);

        @Modifying
        @Query("DELETE FROM Education e WHERE e.candidate.id = :candidateId")
        void deleteByCandidateId(@Param("candidateId") Long candidateId);
}
