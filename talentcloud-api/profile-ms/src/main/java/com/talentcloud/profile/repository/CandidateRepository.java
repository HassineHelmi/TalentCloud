
package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Candidate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @EntityGraph(attributePaths = {"skills", "educations", "experiences", "certifications"})
    Optional<Candidate> findByProfileUserId(Long profileUserId);

    @EntityGraph(attributePaths = {"skills", "educations", "experiences", "certifications"})
    Optional<Candidate> findById(Long id);


}