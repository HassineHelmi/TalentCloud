package com.talentcloud.profile.dto;

import com.talentcloud.profile.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CandidatePublicProfileDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String resumeUrl;
    private String jobTitle;
    private String jobCategory;
    private VisibilitySettings visibilitySettings;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Certification> certifications;
    private List<Skills> skills;
    private boolean isBlocked;
}