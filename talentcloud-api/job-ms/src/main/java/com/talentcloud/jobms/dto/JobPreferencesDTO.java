package com.talentcloud.jobms.dto;


import com.talentcloud.jobms.model.JobPreferences;
import jakarta.validation.constraints.NotNull;

public record JobPreferencesDTO(
        Long id,
        @NotNull Long candidateId,
        String preferredRoles,
        String preferredLocations,
        String preferredIndustries,
        boolean alertsEnabled
) {

    public static JobPreferencesDTO fromEntity(JobPreferences prefs) {
        return new JobPreferencesDTO(
                prefs.getId(),
                prefs.getCandidateId(),
                prefs.getPreferredRoles(),
                prefs.getPreferredLocations(),
                prefs.getPreferredIndustries(),
                prefs.isAlertsEnabled()
        );
    }
}