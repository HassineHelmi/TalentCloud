package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du poste est obligatoire.")
    @Column(name = "job_title") // Renamed to match schema
    private String jobTitle;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    @Column(name = "company_name") // Renamed to match schema's `entreprise` (for website)
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
            regexp = "^(CDI|CDD|Freelance|Stage|Alternance)?$",
            message = "Type de contrat invalide. Les valeurs valides sont : CDI, CDD, Freelance, Stage, Alternance."
    )
    @Column(name = "contract_type")
    private String contractType;

    @Column(length = 1000)
    private String technologies;

    // Relation to Candidate
    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonBackReference(value="candidate-experiences")
    private Candidate candidate;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(insertable = false)
    private LocalDateTime updated_at;
}