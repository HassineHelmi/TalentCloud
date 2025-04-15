package com.talentcloud.profile.iservice;

import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.dto.UpdateSkillsDto;

import java.util.Optional;

public interface IServiceSkills {

    Optional<Skills> getSkillsByCandidateId(Long candidateId);

    Skills addSkills(Skills skills, Long candidateId);

    Skills updateSkills(Long skillsId, UpdateSkillsDto updateSkillsDto);  // Updated method signature

    void deleteSkills(Long skillsId);
}
