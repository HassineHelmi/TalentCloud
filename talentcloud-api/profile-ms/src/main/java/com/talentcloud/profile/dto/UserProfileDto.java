package com.talentcloud.profile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.talentcloud.profile.model.Admin;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.model.Profile;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Ensures that null fields (like other roles) are not in the JSON response
public class UserProfileDto {

    private Profile profile;
    private List<String> roles;
    private Candidate candidateDetails;
    private Client clientDetails;
    private Admin adminDetails;
}