package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
