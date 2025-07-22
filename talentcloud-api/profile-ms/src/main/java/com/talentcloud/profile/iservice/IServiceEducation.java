package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateEducationDto;
import com.talentcloud.profile.model.Education;

import java.util.List;

public interface IServiceEducation {

    Education addEducation(Education education, Long candidateId);

    Education editEducation(Long educationId, UpdateEducationDto dto);

    void deleteEducation(Long educationId);

    List<Education> getAllEducationByCandidateId(Long candidateId);
}
