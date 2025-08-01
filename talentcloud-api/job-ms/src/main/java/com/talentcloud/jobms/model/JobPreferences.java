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
    @Column(unique = true)
    private Long candidateId; // Foreign key to the Candidate in the profile-ms

    private String preferredRoles;
    private String preferredLocations;
    private String preferredIndustries;
    private boolean alertsEnabled;
}