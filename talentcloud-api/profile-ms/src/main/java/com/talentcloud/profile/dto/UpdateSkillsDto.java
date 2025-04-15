package com.talentcloud.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSkillsDto {
    @NotBlank(message = "Programming languages are required.")
    private String programmingLanguages;
    private String softSkills;
    private String technicalSkills;
    private String toolsAndTechnologies;
    private String customSkills;
}
