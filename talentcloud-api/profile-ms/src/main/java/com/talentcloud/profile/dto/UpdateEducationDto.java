package com.talentcloud.profile.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateEducationDto {

    private String institution;
    private String diplome;
    private String domaineEtude;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean enCour;
}