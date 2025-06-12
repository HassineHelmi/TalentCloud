package com.talentcloud.profile.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateExperienceDto {
    private String jobTitle;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String location;
    private Boolean isCurrent;
    private String company;
    private String contractType;
    private String technologies;
}