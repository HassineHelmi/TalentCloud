package com.talentcloud.profile.model;

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
@Table(name = "experience") // Table name in schema is 'experience' (singular)
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
    @Column(name = "company_name") // Renamed to match schema
    private String companyName;

    @NotNull(message = "La date de début est obligatoire.")
    @Column(name = "date_debut") // Renamed to match schema
    private LocalDate dateDebut;

    @Column(name = "date_fin") // Renamed to match schema
    private LocalDate dateFin;

    @Column(length = 1000)
    private String description;

    @Column(name = "location") // Renamed to match schema
    private String location;

    @NotNull(message = "Le champ 'enCours' doit être spécifié.")
    @Column(name = "is_current") // Renamed to match schema
    private Boolean isCurrent; // Renamed to match schema

    @URL(message = "Veuillez fournir une URL valide pour le site de l'entreprise.")
    @Column(name = "entreprise") // Renamed to match schema's `entreprise` (for website)
    private String entreprise;

    @Pattern(
            regexp = "^(CDI|CDD|Freelance|Stage|Alternance)?$",
            message = "Type de contrat invalide. Les valeurs valides sont : CDI, CDD, Freelance, Stage, Alternance."
    )
    @Column(name = "type_contract") // Renamed to match schema
    private String typeContract;

    @Column(length = 1000)
    private String technologies;

    // Relation to Candidate
    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
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