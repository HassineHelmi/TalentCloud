package com.profile_service.profile_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user_profiles") // Correspond à la table en BDD
public class UserProfile {

    @Id
    private Long id;
    private String email;
    private String name;
    private String role;
    // private String preferences;

}
