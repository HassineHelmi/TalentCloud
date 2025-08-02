package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByBlockedFalse();
    Optional<Client> findByProfileUserId(Long profileUserId);
}
