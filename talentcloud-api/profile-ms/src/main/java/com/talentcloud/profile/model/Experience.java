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
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du poste est obligatoire.")
    private String titrePoste;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    private String entreprise;

    @NotNull(message = "La date de début est obligatoire.")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(length = 1000)
    private String description;

    private String lieu;

    @NotNull(message = "Le champ 'enCours' doit être spécifié.")
    private Boolean enCours;

    // ✅ NEW FIELDS

    @URL(message = "Veuillez fournir une URL valide pour le site de l'entreprise.")
    private String siteEntreprise;

    @Pattern(
            regexp = "^(CDI|CDD|Freelance|Stage|Alternance)?$",
            message = "Type de contrat invalide. Les valeurs valides sont : CDI, CDD, Freelance, Stage, Alternance."
    )
    private String typeContrat;

    @Column(length = 1000)
    private String technologies;

    // 📌 Relation to Candidate
    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore  // 🛑 Prevents infinite loop during serialization
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
