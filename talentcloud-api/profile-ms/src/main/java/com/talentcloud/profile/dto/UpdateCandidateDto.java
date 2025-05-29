package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.Education;
import com.talentcloud.profile.model.Experience;
import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.model.VisibilitySettings;
import lombok.Data;

@Data
public class UpdateCandidateDto {
    private String resume_url;
    private String jobPreference;
    private String jobTitle;
    private VisibilitySettings visibilitySettings;
    private Skills skills;
    private Education education;
    private Experience experience;

}