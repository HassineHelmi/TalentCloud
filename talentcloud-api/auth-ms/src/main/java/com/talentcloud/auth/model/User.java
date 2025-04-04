package com.talentcloud.auth.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user")
public class User {
    private String id;
    private String username;
    private String email;
    private String role;
    private String keycloakId;
}
