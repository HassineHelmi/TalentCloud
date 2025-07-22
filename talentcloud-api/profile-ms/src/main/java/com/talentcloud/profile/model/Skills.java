package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
// import com.fasterxml.jackson.annotation.JsonIgnore; // No longer needed if using JsonBackReference
import jakarta.persistence.*;
import lombok.*; // Import specific annotations
import org.hibernate.proxy.HibernateProxy; // For robust equals/hashCode
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(name = "programming_languages")
    private String programmingLanguages;
    @Column(name = "soft_skills")
    private String softSkills;
    @Column(name = "technical_skills")
    private String technicalSkill;
    @Column(name = "tools_and_technologies")
    private String toolsAndTechnologies;

    @Column(name = "custom_skills")
    private String customSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    @JsonBackReference(value="candidate-skills") // Ensure this value matches JsonManagedReference in Candidate
    private Candidate candidate;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(insertable = false)
    private LocalDateTime updated_at;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Skills skills = (Skills) o;
        return getId() != null && Objects.equals(getId(), skills.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Skills{" +
                "id=" + id +
                ", programmingLanguage='" + programmingLanguages + '\'' +
                ", softSkills='" + softSkills + '\'' +
                ", technicalSkill='" + technicalSkill + '\'' +
                ", toolsAndTechnologies='" + toolsAndTechnologies + '\'' +
                ", customSkill='" + customSkills + '\'' +
                // Avoid recursion by only printing the candidate's ID if needed
                (candidate != null ? ", candidate_id=" + candidate.getId() : "") +
                ", createdAt=" + created_at +
                ", updatedAt=" + updated_at +
                '}';
    }
}