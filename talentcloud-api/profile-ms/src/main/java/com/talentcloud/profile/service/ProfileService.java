package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UserProfileDto;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    public UserProfileDto getProfile(String id) {
        return new UserProfileDto(id, "John Doe", "john@example.com", "Engineer", "Paris");
    }

    public void saveProfile(UserProfileDto dto) {
        // Persist profile logic
    }
}
