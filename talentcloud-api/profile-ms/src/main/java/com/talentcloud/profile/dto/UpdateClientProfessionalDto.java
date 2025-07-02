package com.talentcloud.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UpdateClientProfessionalDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String industry;
    private String country;

    @URL(message = "Please provide a valid website URL")
    private String website;

    @URL(message = "Please provide a valid LinkedIn URL")
    private String linkedInUrl;

    private String companyDescription;
}
