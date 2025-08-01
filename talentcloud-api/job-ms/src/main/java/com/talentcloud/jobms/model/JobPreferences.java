package com.talentcloud.jobms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "job_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "candidate_auth_id", unique = true)
    private String candidateAuthId;

    @Column(name = "preferred_roles", columnDefinition = "TEXT")
    private String preferredRolesStr;

    @Column(name = "preferred_locations", columnDefinition = "TEXT")
    private String preferredLocationsStr;

    @Column(name = "preferred_industries", columnDefinition = "TEXT")
    private String preferredIndustriesStr;

    @Column(name = "alerts_enabled")
    private Boolean alertsEnabled;

    // Utility methods for List<String> conversion
    public List<String> getPreferredRoles() {
        return stringToList(preferredRolesStr);
    }

    public void setPreferredRoles(List<String> roles) {
        this.preferredRolesStr = listToString(roles);
    }

    public List<String> getPreferredLocations() {
        return stringToList(preferredLocationsStr);
    }

    public void setPreferredLocations(List<String> locations) {
        this.preferredLocationsStr = listToString(locations);
    }

    public List<String> getPreferredIndustries() {
        return stringToList(preferredIndustriesStr);
    }

    public void setPreferredIndustries(List<String> industries) {
        this.preferredIndustriesStr = listToString(industries);
    }

    private List<String> stringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(str.split(","));
    }

    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }
}