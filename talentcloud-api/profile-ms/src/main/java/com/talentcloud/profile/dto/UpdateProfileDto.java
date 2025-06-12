package com.talentcloud.profile.dto;


import lombok.Data;

@Data
public class UpdateProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;


}
