package com.talentcloud.profile.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateExperienceDto {
    private String jobTitle; // Renamed
    private String companyName; // Renamed
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private String location; // Renamed
    private Boolean isCurrent; // Renamed
    private String entreprise; // Renamed (for website)
    private String typeContract; // Renamed
    private String technologies;
}