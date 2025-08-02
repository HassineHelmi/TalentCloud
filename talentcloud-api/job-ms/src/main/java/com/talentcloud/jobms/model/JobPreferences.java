package com.talentcloud.jobms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "job_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "candidate_auth_id", unique = true)
    private String candidateAuthId; // Changed from Long candidateId

    private String preferredRoles;
    private String preferredLocations;
    private String preferredIndustries;
    private boolean alertsEnabled;
}