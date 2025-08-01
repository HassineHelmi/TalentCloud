package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.JobPreferences;
import java.util.List;

public record JobPreferencesDTO(
        Long id,
        String candidateAuthId,
        List<String> preferredRoles,
        List<String> preferredLocations,
        List<String> preferredIndustries,
        Boolean alertsEnabled
) {
    public static JobPreferencesDTO fromEntity(JobPreferences prefs) {
        return new JobPreferencesDTO(
                prefs.getId(),
                prefs.getCandidateAuthId(),
                prefs.getPreferredRoles(),
                prefs.getPreferredLocations(),
                prefs.getPreferredIndustries(),
                prefs.getAlertsEnabled()
        );
    }
}