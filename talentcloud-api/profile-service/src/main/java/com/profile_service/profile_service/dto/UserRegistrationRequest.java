package com.profile_service.profile_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String role;


}
