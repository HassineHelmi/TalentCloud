package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*; // Import specific annotations
import org.hibernate.proxy.HibernateProxy; // For robust equals/hashCode
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet; // Import HashSet
import java.util.Objects;   // Import Objects
import java.util.Set;

@Entity
@Table(name = "candidates")
@Getter                          // Add
@Setter                          // Add
@NoArgsConstructor               // Add
@AllArgsConstructor              // Add
// @Data                        // REMOVE!
@EntityListeners(AuditingEntityListener.class)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", unique = true, nullable = false)
    private Long profileUserId;

    private String resume_url;

    @Column(columnDefinition = "TEXT")
    private String jobPreference;


    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_setting")
    private VisibilitySettings visibilitySetting;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // Explicit EAGER is fine if EntityGraph is also used
    @JsonManagedReference(value="candidate-educations") // Unique value for JsonManagedReference if multiple collections of same type on other end
    private Set<Education> educations = new HashSet<>(); // Initialize

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value="candidate-experiences")
    private Set<Experience> experiences = new HashSet<>(); // Initialize

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value="candidate-certifications")
    private Set<Certification> certifications = new HashSet<>(); // Initialize

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true) // orphanRemoval true is good practice
    @JsonManagedReference(value="candidate-skills") // Add a unique value if you have multiple Candidate references in Skills or related entities, otherwise it's optional
    private Set<Skills> skills = new HashSet<>(); // Initialize

    private boolean blocked;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Candidate candidate = (Candidate) o;
        return getId() != null && Objects.equals(getId(), candidate.getId());
    }

    @Override
    public final int hashCode() {
        // return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
        // A common pattern for JPA entities:
        return Objects.hash(id); // Use ID for hash if available, otherwise rely on class hash for transient entities (though super.hashCode() is safer for transient)
        // For persisted entities, Objects.hash(id) is fine.
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", profileUserId=" + profileUserId +
                ", resume_url='" + resume_url + '\'' +
                ", jobPreference='" + jobPreference + '\'' +
                ", visibilitySetting=" + visibilitySetting +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", educationsCount=" + (educations != null ? educations.size() : "null") +
                ", experiencesCount=" + (experiences != null ? experiences.size() : "null") +
                ", certificationsCount=" + (certifications != null ? certifications.size() : "null") +
                ", skillsCount=" + (skills != null ? skills.size() : "null") +
                ", blocked=" + blocked +
                '}';
    }
}