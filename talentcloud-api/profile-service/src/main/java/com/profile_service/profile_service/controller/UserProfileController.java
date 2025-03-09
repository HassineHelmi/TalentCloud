package com.profile_service.profile_service.controller;
import com.profile_service.profile_service.model.UserProfile;
import com.profile_service.profile_service.service.UserProfileService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ussers")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping("/{id}/profile")
    public Mono<UserProfile> getProfile(@PathVariable Long id) {
        return service.getUserProfile(id);
    }

    @PostMapping
    public Mono<UserProfile> createUser(@RequestBody UserProfile userProfile) {
        return service.createUserProfile(userProfile);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return service.deleteUserProfile(id);
    }

    @GetMapping
    public Flux<UserProfile> getAllUsers() {
        return service.getAllUsers();
    }
}