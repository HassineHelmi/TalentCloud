package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du poste est obligatoire.")
    @Column(name = "job_title")
    private String jobTitle;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    @Column(name = "company_name")
    private String companyName;

    @NotNull(message = "La date de début est obligatoire.")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 1000)
    private String description;

    @Column(name = "location")
    private String location;

    @NotNull(message = "Le champ 'enCours' doit être spécifié.")
    @Column(name = "is_current")
    private Boolean isCurrent;

    @Pattern(
            regexp = "^(CDI|CDD|Freelance|Internship)?$",
            message = "Type de contrat invalide. Les valeurs valides sont : CDI, CDD, Freelance, Internship."
    )
    @Column(name = "contract_type")
    private String contractType;

    @Column(length = 1000)
    private String technologies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonBackReference(value="candidate-experiences")
    private Candidate candidate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updated_at;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Experience that = (Experience) o;
        return Objects.equals(id, that.id) && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(companyName, that.companyName) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(description, that.description) && Objects.equals(location, that.location) && Objects.equals(isCurrent, that.isCurrent) && Objects.equals(contractType, that.contractType) && Objects.equals(technologies, that.technologies) && Objects.equals(candidate, that.candidate) && Objects.equals(created_at, that.created_at) && Objects.equals(updated_at, that.updated_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobTitle, companyName, startDate, endDate, description, location, isCurrent, contractType, technologies, candidate, created_at, updated_at);
    }
}
