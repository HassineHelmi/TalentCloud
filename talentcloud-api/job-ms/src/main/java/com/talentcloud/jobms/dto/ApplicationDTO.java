package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.Application;
import com.talentcloud.jobms.model.ApplicationStatus;

import java.time.Instant;

public record ApplicationDTO(
        Long id,
        String candidateAuthId,
        Long jobId,
        ApplicationStatus status,
        Instant appliedAt,
        Double jobFitScore
) {
    public static ApplicationDTO fromEntity(Application app) {
        return new ApplicationDTO(
                app.getId(),
                app.getCandidateAuthId(),
                app.getJob().getId(),
                app.getStatus(),
                app.getAppliedAt(),
                app.getJobFitScore()
        );
    }
}