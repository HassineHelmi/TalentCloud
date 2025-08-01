package com.talentcloud.jobms.repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.talentcloud.jobms.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByCandidateId(Long candidateId);

    Optional<Application> findByJobIdAndCandidateId(Long jobId, Long candidateId);
}
