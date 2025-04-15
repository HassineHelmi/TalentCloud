package com.talentcloud.profile.repository;

import com.talentcloud.profile.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByBlockedFalse();  // Fetch only clients that are not blocked

}
