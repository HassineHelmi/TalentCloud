package com.talentcloud.profile.iservice;


import com.talentcloud.profile.dto.UpdateExperienceDto;
import com.talentcloud.profile.model.Experience;

import java.util.List;
import java.util.Optional;

public interface IServiceExperience {
    Experience createExperience(Experience experience, Long candidateId);
    Experience updateExperience(Long experienceId, UpdateExperienceDto dto);
    Experience deleteExperience(Long experienceId);
    Optional<Experience> getExperienceById(Long experienceId);
    List<Experience> getAllExperiencesByCandidateId(Long candidateId);
}

