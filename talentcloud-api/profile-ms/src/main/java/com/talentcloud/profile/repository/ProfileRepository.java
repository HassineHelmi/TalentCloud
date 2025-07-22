package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAuthServiceUserId(String authServiceUserId);
    Optional<Profile> findByEmail(String email);
}

