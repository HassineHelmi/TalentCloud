package com.talentcloud.jobms.service;

import com.talentcloud.jobms.dto.CreateJobPreferencesDTO;
import com.talentcloud.jobms.dto.JobPreferencesDTO;
import com.talentcloud.jobms.model.JobPreferences;
import com.talentcloud.jobms.repository.JobPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPreferencesService {

    private final JobPreferencesRepository preferencesRepository;

    @Transactional
    public JobPreferencesDTO saveOrUpdatePreferences(CreateJobPreferencesDTO dto, String candidateAuthId) {
        JobPreferences prefs = preferencesRepository.findByCandidateAuthId(candidateAuthId)
                .orElse(new JobPreferences());

        prefs.setCandidateAuthId(candidateAuthId);
        prefs.setPreferredRoles(listToString(dto.preferredRoles()));
        prefs.setPreferredLocations(listToString(dto.preferredLocations()));
        prefs.setPreferredIndustries(listToString(dto.preferredIndustries()));
        prefs.setAlertsEnabled(dto.alertsEnabled());

        JobPreferences savedPrefs = preferencesRepository.save(prefs);
        return JobPreferencesDTO.fromEntity(savedPrefs);
    }

    @Transactional(readOnly = true)
    public JobPreferencesDTO getPreferences(String candidateAuthId) {
        return preferencesRepository.findByCandidateAuthId(candidateAuthId)
                .map(JobPreferencesDTO::fromEntity)
                .orElse(new JobPreferencesDTO(null, candidateAuthId, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), false));
    }

    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    public static List<String> stringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.split(","));
    }
}