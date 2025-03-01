package com.profile_service.profile_service.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_profiles")
public class UserProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String role;
    private String preferences;

    public UserProfile() {}

    public UserProfile(String email, String name, String role, String preferences) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.preferences = preferences;
    }

    // ✅ Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
