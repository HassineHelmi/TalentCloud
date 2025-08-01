package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.Application;
import com.talentcloud.jobms.model.ApplicationStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ApplicationDTO(
        Long id,
        Long candidateId,
        Long jobId,
        ApplicationStatus status,
        LocalDateTime appliedAt,
        Double jobFitScore
) {

    public static ApplicationDTO fromEntity(Application app) {
        return new ApplicationDTO(
                app.getId(),
                app.getCandidateId(),
                app.getJob().getId(),
                app.getStatus(),
                app.getAppliedAt().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                app.getJobFitScore()
        );
    }
}