package com.talentcloud.jobms.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.Instant;

@Entity
@Table(name = "evaluation_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed to Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "stage_name")
    private String stageName;

    @Column(name = "stage_order")
    private Integer stageOrder;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}