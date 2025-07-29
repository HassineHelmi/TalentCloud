package com.talentcloud.profile.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "auth_service_user_id", unique = true, nullable = false)
    private String authServiceUserId;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;
    @Column(name = "linkedin_url")
    private String linkedInUrl;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id) && Objects.equals(authServiceUserId, profile.authServiceUserId) && Objects.equals(email, profile.email) && Objects.equals(firstName, profile.firstName) && Objects.equals(lastName, profile.lastName) && Objects.equals(phoneNumber, profile.phoneNumber) && Objects.equals(address, profile.address) && Objects.equals(linkedInUrl, profile.linkedInUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authServiceUserId, email, firstName, lastName, phoneNumber, address, linkedInUrl);
    }
}
