package com.profile_service.profile_service.repository;

import com.profile_service.profile_service.model.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Long> {
    Mono<UserProfile> findByEmail(String email);
}
