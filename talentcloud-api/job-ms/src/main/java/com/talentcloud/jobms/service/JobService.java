package com.talentcloud.jobms.service;

import com.talentcloud.jobms.dto.CreateJobDTO;
import com.talentcloud.jobms.dto.JobDTO;
import com.talentcloud.jobms.dto.UpdateJobDto;
import com.talentcloud.jobms.exception.JobNotFoundException;
import com.talentcloud.jobms.model.Job;
import com.talentcloud.jobms.repository.JobRepository;
import com.talentcloud.jobms.service.IService.IJobService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobDTO createJob(CreateJobDTO dto) {
        Job job = new Job();
        job.setTitle(dto.title());
        job.setDescription(dto.description());
        job.setLocation(dto.location());
        job.setContractType(dto.contractType());
        job.setClientId(dto.clientId());
        job.setDeadline(dto.deadline());
        job.setActive(true);
        Job savedJob = jobRepository.save(job);
        return JobDTO.fromEntity(savedJob);
    }

    @Transactional
    public List<JobDTO> getAllActiveJobs() {
        return jobRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(JobDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobDTO getJobById(Long id) {
        return jobRepository.findById(id)
                .map(JobDTO::fromEntity)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
    }

    @Transactional
    public JobDTO updateJob(Long id, CreateJobDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        job.setTitle(dto.title());
        job.setDescription(dto.description());
        job.setLocation(dto.location());
        job.setContractType(dto.contractType());
        job.setDeadline(dto.deadline());

        Job updatedJob = jobRepository.save(job);
        return JobDTO.fromEntity(updatedJob);
    }

    @Transactional
    public void deactivateJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        job.setActive(false);
        job.setDeadline(null);
        jobRepository.save(job);
    }
}