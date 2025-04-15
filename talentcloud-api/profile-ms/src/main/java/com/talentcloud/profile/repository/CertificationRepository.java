package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    // Corrected method to find by candidate's relationship, not by ID
    List<Certification> findByCandidate(Candidate candidate);  // Correct query to find certifications for a specific candidate}
}