package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.VisibilitySettings;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCandidateDto {
    private String resumeUrl;
    private String jobTitle;
    private String jobCategory;

    @NotNull(message = "Visibility settings must be provided")
    private VisibilitySettings visibilitySettings;
}