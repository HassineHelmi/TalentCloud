package com.talentcloud.jobms.service;


import com.talentcloud.jobms.dto.JobPreferencesDTO;
import com.talentcloud.jobms.model.JobPreferences;
import com.talentcloud.jobms.repository.JobPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobPreferencesService {

    private final JobPreferencesRepository preferencesRepository;

    @Transactional
    public JobPreferencesDTO saveOrUpdatePreferences(JobPreferencesDTO dto) {
        JobPreferences prefs = preferencesRepository.findByCandidateId(dto.candidateId())
                .orElse(new JobPreferences());

        prefs.setCandidateId(dto.candidateId());
        prefs.setPreferredRoles(dto.preferredRoles());
        prefs.setPreferredLocations(dto.preferredLocations());
        prefs.setPreferredIndustries(dto.preferredIndustries());
        prefs.setAlertsEnabled(dto.alertsEnabled());

        JobPreferences savedPrefs = preferencesRepository.save(prefs);
        return JobPreferencesDTO.fromEntity(savedPrefs);
    }

    @Transactional(readOnly = true)
    public JobPreferencesDTO getPreferences(Long candidateId) {
        return preferencesRepository.findByCandidateId(candidateId)
                .map(JobPreferencesDTO::fromEntity)
                .orElse(new JobPreferencesDTO(null, candidateId, null, null, null, false));
    }
}