package com.talentcloud.profile.model;

import lombok.*;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false, unique = true)
    private Long profileUserId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(id, admin.id) && Objects.equals(profileUserId, admin.profileUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, profileUserId);
    }
}