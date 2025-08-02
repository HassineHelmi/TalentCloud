package com.talentcloud.jobms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateJobDto {
    private String title;
    private String description;
    private String requirements;
    private LocalDate applicationDeadline;
    private Boolean active;
}