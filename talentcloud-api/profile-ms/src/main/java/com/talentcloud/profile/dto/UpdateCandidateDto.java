package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.Education;
import com.talentcloud.profile.model.Experience;
import com.talentcloud.profile.model.Skills;
import com.talentcloud.profile.model.VisibilitySettings;
import lombok.Data;

@Data
public class UpdateCandidateDto {

    private String resumeUrl;
    private String jobPreferences;
    private VisibilitySettings visibilitySettings;

}