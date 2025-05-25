package com.talentcloud.auth.repository;

import com.talentcloud.auth.model.User;
import org.springframework.data.r2dbc.repository.Query; // <--- ADD THIS IMPORT
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);

    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE username = $1)")
    Mono<Boolean> existsByUsername(String username);

    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE email = $1)")
    Mono<Boolean> existsByEmail(String email);
}