package com.talentcloud.jobms.repository;

import com.talentcloud.jobms.model.JobPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPreferencesRepository extends JpaRepository<JobPreferences, Long> {
    Optional<JobPreferences> findByCandidateAuthId(String candidateAuthId);
}