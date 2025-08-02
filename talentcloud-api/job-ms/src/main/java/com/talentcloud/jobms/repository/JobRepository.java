package com.talentcloud.jobms.repository;

import com.talentcloud.jobms.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByActiveTrue();
    List<Job> findByClientAuthId(String clientAuthId);
    List<Job> findByActiveTrueOrderByCreatedAtDesc();
}