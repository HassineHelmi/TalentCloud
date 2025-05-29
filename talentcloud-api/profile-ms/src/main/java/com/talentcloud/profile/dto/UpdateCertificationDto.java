package com.talentcloud.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import org.hibernate.validator.constraints.URL; // Added import for URL

@Getter
@Setter
public class UpdateCertificationDto {

    @NotBlank(message = "Nom is required")
    private String nom;
    @NotBlank(message = "Organisme is required")
    private String organisme;
    @NotNull(message = "Date d'obtention is required")
    private LocalDate dateObtention;


    @URL(message = "Veuillez fournir une URL valide pour la certification") // Added annotation
    private String certificationUrl; // Renamed
}