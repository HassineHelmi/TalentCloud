package com.talentcloud.auth.model;

import lombok.Data;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("users")
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    private String role;
    @Column("keycloak_id")
    private String keycloakId;
}
