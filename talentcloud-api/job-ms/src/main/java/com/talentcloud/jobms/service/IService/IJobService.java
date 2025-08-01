package com.talentcloud.jobms.service.IService;

import com.talentcloud.jobms.dto.CreateJobDTO;
import com.talentcloud.jobms.dto.JobDTO;
import com.talentcloud.jobms.dto.UpdateJobDto;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    JobDTO createJob(CreateJobDTO createJobDto);
    List<JobDTO> findAllJobs();
    Optional<JobDTO> findJobById(Long id);
    JobDTO updateJob(Long id, UpdateJobDto updateJobDto);
    void deleteJob(Long id);
}
