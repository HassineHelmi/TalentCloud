package com.talentcloud.jobms.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long candidateId; // Foreign key to the Candidate in the profile-ms

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;


    private Double jobFitScore; // For AI-Based Job Fit Scoring

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_stage_id") // Can be nullable
    private EvaluationStage currentStage;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private Instant appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
