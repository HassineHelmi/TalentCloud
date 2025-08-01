package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.ContractType;
import com.talentcloud.jobms.model.Job;

import java.time.LocalDateTime;

public record JobDTO(
        Long id,
        String title,
        String description,
        String location,
        ContractType contractType,
        Long clientId,
        boolean active,
        LocalDateTime deadline,
        LocalDateTime createdAt
) {
    /**
     * Converts a Job entity to a JobDTO.
     * @param job The Job entity.
     * @return A new JobDTO instance.
     */
    public static JobDTO fromEntity(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getContractType(),
                job.getClientId(),
                job.isActive(),
                job.getDeadline(),
                job.getCreatedAt()
        );
    }
}