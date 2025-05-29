package com.talentcloud.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSkillsDto {
    @NotBlank(message = "Programming languages are required.")
    private String programmingLanguage;
    private String softSkills;
    private String technicalSkill;
    private String toolsAndTechnologies;
    private String customSkill;
}