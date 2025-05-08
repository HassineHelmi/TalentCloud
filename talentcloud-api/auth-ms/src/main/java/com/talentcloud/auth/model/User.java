package com.talentcloud.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String role;
    @Column("keycloak_id")
    private String keycloakId;
}
