package com.talentcloud.profile.dto;

import java.time.LocalDate;

public record EducationRequest(
        String institutionName,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,

        Boolean isCurrent // Renamed
) {}