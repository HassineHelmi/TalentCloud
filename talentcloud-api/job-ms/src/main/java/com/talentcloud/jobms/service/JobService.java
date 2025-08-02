package com.talentcloud.jobms.service;

import com.talentcloud.jobms.dto.CreateJobDTO;
import com.talentcloud.jobms.dto.JobDTO;
import com.talentcloud.jobms.exception.JobNotFoundException;
import com.talentcloud.jobms.model.Job;
import com.talentcloud.jobms.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobDTO createJob(CreateJobDTO dto, String clientAuthId) {
        Job job = new Job();
        job.setTitle(dto.title());
        job.setDescription(dto.description());
        job.setLocation(dto.location());
        job.setContractType(dto.contractType());
        job.setDeadline(dto.deadline());
        job.setClientAuthId(clientAuthId);

        Job savedJob = jobRepository.save(job);
        return JobDTO.fromEntity(savedJob);
    }

    @Transactional(readOnly = true)
    public List<JobDTO> getAllActiveJobs() {
        return jobRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobDTO getJobById(Long id) {
        return jobRepository.findById(id)
                .map(JobDTO::fromEntity)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
    }

    @Transactional
    public JobDTO updateJob(Long id, CreateJobDTO dto, Jwt principal) {
        Job job = findJobByIdAndAuthorize(id, principal);
        job.setTitle(dto.title());
        job.setDescription(dto.description());
        job.setLocation(dto.location());
        job.setContractType(dto.contractType());
        job.setDeadline(dto.deadline());

        Job updatedJob = jobRepository.save(job);
        return JobDTO.fromEntity(updatedJob);
    }

    @Transactional
    public void deactivateJob(Long id, Jwt principal) {
        Job job = findJobByIdAndAuthorize(id, principal);
        job.setActive(false);
        jobRepository.save(job);
    }

    @Transactional
    public JobDTO reactivateJob(Long id, Jwt principal) {
        Job job = findJobByIdAndAuthorize(id, principal);

        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot reactivate a job whose deadline has already passed.");
        }

        job.setActive(true);
        Job updatedJob = jobRepository.save(job);
        return JobDTO.fromEntity(updatedJob);
    }

    private Job findJobByIdAndAuthorize(Long jobId, Jwt principal) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ADMIN"));

        if (!isAdmin && !job.getClientAuthId().equals(principal.getSubject())) {
            throw new AccessDeniedException("You do not have permission to modify this job.");
        }
        return job;
    }
}