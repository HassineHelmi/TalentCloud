package com.talentcloud.profile.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email; // Keep if this email is validated as such
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull; // NotNull not typically used on ID
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects; // For robust equals/hashCode
import org.hibernate.proxy.HibernateProxy; // For robust equals/hashCode

@Entity
@Table(name = "clients")
@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Maps to 'id' (PK) in 'clients' table

    @Column(name = "profile_id", unique = true, nullable = false)
    private Long profileUserId; // Maps to 'profile_id' (FK to profiles.id)

    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false) // Assuming company_name cannot be null based on schema
    private String companyName;

    @Column(name = "industry")
    private String industry;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email") // Company's contact email
    @Email // Optional: if this email should be validated
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "linked_in_url")
    private String linkedInUrl;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "blocked", nullable = false) // Assuming blocked has a default and cannot be null
    private boolean blocked = false; // Default value

    // Consider overriding equals and hashCode for JPA entities like in Candidate for robustness
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Client client = (Client) o;
        return getId() != null && Objects.equals(getId(), client.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}