package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.VisibilitySettings;
import lombok.Data;

@Data
public class UpdateCandidateDto {

    private String resumeUrl;
    private String jobTitle;
    private String jobCategory;
    private VisibilitySettings visibilitySettings;

}