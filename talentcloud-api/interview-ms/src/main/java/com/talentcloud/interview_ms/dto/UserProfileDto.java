package com.talentcloud.interview_ms.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String id;
    private String name;
    private String email;
    private String jobTitle;
    private String location;
}
