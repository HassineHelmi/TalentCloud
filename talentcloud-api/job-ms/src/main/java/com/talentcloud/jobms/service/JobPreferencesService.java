package com.talentcloud.jobms.service;

import com.talentcloud.jobms.dto.CreateJobPreferencesDTO;
import com.talentcloud.jobms.dto.JobPreferencesDTO;
import com.talentcloud.jobms.model.JobPreferences;
import com.talentcloud.jobms.repository.JobPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class JobPreferencesService {

    private final JobPreferencesRepository preferencesRepository;

    @Transactional
    public JobPreferencesDTO saveOrUpdatePreferences(CreateJobPreferencesDTO dto, String candidateAuthId) {
        JobPreferences prefs = preferencesRepository.findByCandidateAuthId(candidateAuthId)
                .orElse(new JobPreferences());

        prefs.setCandidateAuthId(candidateAuthId);
        prefs.setPreferredRoles(dto.preferredRoles());
        prefs.setPreferredLocations(dto.preferredLocations());
        prefs.setPreferredIndustries(dto.preferredIndustries());
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
}