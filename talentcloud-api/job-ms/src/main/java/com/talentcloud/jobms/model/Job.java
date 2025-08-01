package com.talentcloud.jobms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Lob
    private String description;

    @NotBlank
    private String location;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @NotNull
    private Long clientId; // Foreign key to the Client in the profile-ms

    private boolean active;

    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationStage> evaluationStages;
}
