package com.talentcloud.jobms.service;

import com.talentcloud.jobms.dto.ApplicationDTO;
import com.talentcloud.jobms.dto.CreateApplicationDTO;
import com.talentcloud.jobms.exception.ApplicationNotFoundException;
import com.talentcloud.jobms.exception.JobNotFoundException;
import com.talentcloud.jobms.model.Application;
import com.talentcloud.jobms.model.ApplicationStatus;
import com.talentcloud.jobms.model.Job;
import com.talentcloud.jobms.repository.ApplicationRepository;
import com.talentcloud.jobms.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @Transactional
    public ApplicationDTO submitApplication(CreateApplicationDTO dto, String candidateAuthId) {
        if (applicationRepository.existsByJobIdAndCandidateAuthId(dto.jobId(), candidateAuthId)) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        Job job = jobRepository.findById(dto.jobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + dto.jobId()));

        Application app = new Application();
        app.setJob(job);
        app.setCandidateAuthId(candidateAuthId);
        app.setJobFitScore(Math.round(Math.random() * 50 + 50) / 1.0); // Placeholder

        Application savedApp = applicationRepository.save(app);
        return ApplicationDTO.fromEntity(savedApp);
    }

    @Transactional
    public ApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatus status, Jwt principal) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        authorizeClientOrAdmin(principal, app.getJob().getClientAuthId());

        app.setStatus(status);
        Application updatedApp = applicationRepository.save(app);
        return ApplicationDTO.fromEntity(updatedApp);
    }

    @Transactional
    public ApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));
        app.setStatus(status);
        Application updatedApp = applicationRepository.save(app);
        return ApplicationDTO.fromEntity(updatedApp);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsForJob(Long jobId, Jwt principal) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        authorizeClientOrAdmin(principal, job.getClientAuthId());

        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsForCandidate(String candidateAuthId) {
        return applicationRepository.findByCandidateAuthId(candidateAuthId).stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void authorizeClientOrAdmin(Jwt principal, String resourceOwnerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ADMIN"));

        if (!isAdmin && !resourceOwnerId.equals(principal.getSubject())) {
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }
}