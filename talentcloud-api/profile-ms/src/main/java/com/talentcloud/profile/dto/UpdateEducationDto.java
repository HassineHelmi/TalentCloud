package com.talentcloud.profile.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateEducationDto {

    private String institutionName;
    private String degree;
    private String fieldOfStudy;
    private LocalDate StartDate;
    private LocalDate EndDate;
    private Boolean isCurrent;
}