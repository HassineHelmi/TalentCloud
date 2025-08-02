package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByCandidate(Candidate candidate);

    @Modifying
    @Query("DELETE FROM Certification c WHERE c.candidate.id = :candidateId")
    void deleteByCandidateId(@Param("candidateId") Long candidateId);
}