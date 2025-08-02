package com.talentcloud.jobms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_progress")
public class ApplicationProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed to Long

    @Column(name = "application_id")
    private Long applicationId; // Changed to Long

    @Column(name = "stage_id")
    private Long stageId; // Changed to Long

    private String status;
    private String notes;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}