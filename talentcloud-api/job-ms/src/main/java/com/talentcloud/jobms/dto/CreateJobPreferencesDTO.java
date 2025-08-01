package com.talentcloud.jobms.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateJobPreferencesDTO(
        @NotNull(message = "Preferred roles list cannot be null.")
        List<String> preferredRoles,

        @NotNull(message = "Preferred locations list cannot be null.")
        List<String> preferredLocations,

        @NotNull(message = "Preferred industries list cannot be null.")
        List<String> preferredIndustries,

        @NotNull(message = "Alerts enabled flag is required.")
        Boolean alertsEnabled
) {}