package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.VisibilitySettings;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCandidateDto {
    private String resumeUrl;
    private String jobPreferences;

    @NotNull(message = "Visibility settings must be provided")
    private VisibilitySettings visibilitySettings;
}