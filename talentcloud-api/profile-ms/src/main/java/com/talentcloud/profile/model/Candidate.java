package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId; // [cite: 113]

    // --- MODIFIED LINE ---
    @Column(name = "user_id", unique = true, nullable = false) // Added unique and nullable constraints for integrity
    private Long userId; // Changed from String to Long to match auth-ms.User.id [cite: 113]
    // ---------------------

    private String profilePicture; // [cite: 114]

    private String resume; // [cite: 114]

    @Column(columnDefinition = "TEXT")
    private String jobPreferences; // [cite: 114]

    private String jobTitle; // [cite: 115]

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_settings")
    private VisibilitySettings visibilitySettings; // [cite: 115]

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // [cite: 116, 117]

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(insertable = false)
    private LocalDateTime updatedAt; // [cite: 117]

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations; // [cite: 118]

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> experiences; // [cite: 119]

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certification> certifications; // [cite: 120]

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skills> skills; // [cite: 121]

    private boolean blocked; // [cite: 122]
}