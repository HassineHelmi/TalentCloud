package com.profile_service.profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentDTO {
    private Long id;
    private Long userId;
    private String documentType;
}
