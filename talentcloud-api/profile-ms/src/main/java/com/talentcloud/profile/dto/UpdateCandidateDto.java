package com.talentcloud.profile.dto;

import lombok.Data;

@Data
public class UpdateCandidateDto {
    private String profilePicture;
    private String resume;
    private String jobPreferences;
    private String jobTitle;
}
