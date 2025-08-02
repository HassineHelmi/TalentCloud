package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Candidate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @EntityGraph(attributePaths = {"skills", "educations", "experiences", "certifications"})
    Optional<Candidate> findByProfileUserId(Long profileUserId);

    @EntityGraph(attributePaths = {"skills", "educations", "experiences", "certifications"})
    Optional<Candidate> findById(Long id);

    // --- ADD THIS METHOD ---
    @Query("SELECT c FROM Candidate c JOIN Profile p ON c.profileUserId = p.id WHERE p.authServiceUserId = :authId")
    @EntityGraph(attributePaths = {"skills", "educations", "experiences", "certifications"})
    Optional<Candidate> findByProfileAuthServiceUserId(@Param("authId") String authId);

    @Query("SELECT c, p FROM Candidate c JOIN Profile p ON c.profileUserId = p.id")
    List<Object[]> findAllCandidatesWithProfiles();
}