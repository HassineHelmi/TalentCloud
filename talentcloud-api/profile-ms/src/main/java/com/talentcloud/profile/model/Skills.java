package com.talentcloud.profile.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Skills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Predefined Skills Categories
    private String programmingLanguages; // Comma-separated, e.g., Java, Python, C#
    private String softSkills;           // Comma-separated, e.g., Leadership, Problem Solving, Time Management
    private String technicalSkills;      // Comma-separated, e.g., Cloud Computing, Machine Learning
    private String toolsAndTechnologies; // Comma-separated, e.g., Docker, Git, Jenkins

    // Custom Skills: Allow the candidate to freely add any other skill not predefined
    private String customSkills;         // Comma-separated, e.g., Data Visualization, Agile Methodology

    // Relationship to Candidate
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonIgnore
    private Candidate candidate;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
