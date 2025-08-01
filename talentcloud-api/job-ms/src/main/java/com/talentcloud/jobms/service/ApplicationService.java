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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    // Inject an AI service in the future for scoring
    // private final AiService aiService;

    @Transactional
    public ApplicationDTO submitApplication(CreateApplicationDTO dto) {
        if (applicationRepository.findByJobIdAndCandidateId(dto.jobId(), dto.candidateId()).isPresent()) {
            throw new IllegalStateException("Candidate has already applied for this job.");
        }

        Job job = jobRepository.findById(dto.jobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + dto.jobId()));

        Application app = new Application();
        app.setJob(job);
        app.setCandidateId(dto.candidateId());
        app.setStatus(ApplicationStatus.APPLIED);
        // app.setJobFitScore(aiService.calculateFitScore(dto.candidateId(), dto.jobId()));
        app.setJobFitScore(Math.round(Math.random() * 50 + 50) / 1.0); // Placeholder for AI score

        Application savedApp = applicationRepository.save(app);
        // TODO: Notify recruiters about the new application
        return ApplicationDTO.fromEntity(savedApp);
    }

    @Transactional
    public ApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        app.setStatus(status);
        Application updatedApp = applicationRepository.save(app);
        // TODO: Notify candidate and other stakeholders about status change
        return ApplicationDTO.fromEntity(updatedApp);
    }

    @Transactional
    public List<ApplicationDTO> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ApplicationDTO> getApplicationsForCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}