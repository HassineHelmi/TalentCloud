package com.talentcloud.profile.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String id;
    private String name;
    private String email;
    private String jobTitle;
    private String location;

    public UserProfileDto(String id, String johnDoe, String s, String engineer, String paris) {
    }
}
