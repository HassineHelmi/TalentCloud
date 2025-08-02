package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.JobPreferences;
import com.talentcloud.jobms.service.JobPreferencesService;
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
                JobPreferencesService.stringToList(prefs.getPreferredRoles()),
                JobPreferencesService.stringToList(prefs.getPreferredLocations()),
                JobPreferencesService.stringToList(prefs.getPreferredIndustries()),
                prefs.isAlertsEnabled()
        );
    }
}