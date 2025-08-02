package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusUpdateDto {
    private ApplicationStatus newStatus;
}