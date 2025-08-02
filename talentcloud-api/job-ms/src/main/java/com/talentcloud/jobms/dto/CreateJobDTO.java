package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.ContractType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateJobDTO(
        @NotBlank(message = "Job title is required.")
        String title,

        @NotBlank(message = "Job description is required.")
        String description,

        @NotBlank(message = "Location is required.")
        String location,

        @NotNull(message = "Contract type is required.")
        ContractType contractType,

        @FutureOrPresent(message = "Deadline must be in the present or future.")
        LocalDateTime deadline
) {}