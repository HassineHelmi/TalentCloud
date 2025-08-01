package com.talentcloud.jobms.dto;

import jakarta.validation.constraints.NotNull;

public record CreateApplicationDTO(
        @NotNull(message = "Job ID cannot be null.")
        Long jobId
) {}