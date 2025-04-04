package com.talentcloud.profile.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
public class UserProfile {
    @Id
    private String id;
    private String userId;
    private String jobTitle;
    private String skills;
    private String location;
}
