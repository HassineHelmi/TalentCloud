package com.profile_service.profile_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Data
@Table(name = "documents")
public class Document {
    @Id
    private Long id;
    private Long userId;
    private String documentUrl;
    private String documentType;
    private String storageUrl;
}
