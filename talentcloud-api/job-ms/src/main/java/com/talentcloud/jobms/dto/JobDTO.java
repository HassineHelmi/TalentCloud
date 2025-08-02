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
        String clientAuthId,
        boolean active,
        LocalDateTime deadline,
        LocalDateTime createdAt
) {
    public static JobDTO fromEntity(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getContractType(),
                job.getClientAuthId(),
                job.isActive(),
                job.getDeadline(),
                job.getCreatedAt()
        );
    }
}