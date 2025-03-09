package com.profile_service.profile_service.service;

import com.profile_service.profile_service.model.UserProfile;
import com.profile_service.profile_service.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public Mono<UserProfile> getUserProfile(Long id) {
        return repository.findById(id);
    }

    public Mono<UserProfile> createUserProfile(UserProfile userProfile) {
        return repository.save(userProfile);
    }

    public Mono<Void> deleteUserProfile(Long id) {
        return repository.deleteById(id);
    }

    public Flux<UserProfile> getAllUsers() {
        return repository.findAll();
    }
}
