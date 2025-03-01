package com.profile_service.profile_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profile_service.profile_service.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
