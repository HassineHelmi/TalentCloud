package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final IServiceClient serviceClient;

    @Autowired
    public ClientController(IServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getMyClientProfile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        Optional<Client> client = serviceClient.getClientProfileByUserId(userId);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Client> createClientProfile(@RequestBody @Valid Client client, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        if (!userId.equals(client.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Client createdClient = serviceClient.createClientProfile(client);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId, Authentication authentication) {
        Optional<Client> client = serviceClient.getClientById(clientId);
        if (client.isPresent()) {
            Long currentUserId = Long.valueOf(authentication.getName());
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            if (roles.contains("ROLE_ADMIN") || (roles.contains("ROLE_CLIENT") && client.get().getUserId().equals(currentUserId))) {
                return new ResponseEntity<>(client.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Client> editClientProfile(Authentication authentication, @RequestBody @Valid UpdateClientDto dto) {
        Long userId = Long.valueOf(authentication.getName());
        Optional<Client> optionalClient = serviceClient.getClientProfileByUserId(userId);
        if (optionalClient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Long clientId = optionalClient.get().getClientId();

        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (!userId.equals(dto.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Client updatedClient = serviceClient.updateClientProfile(clientId, dto);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = serviceClient.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PutMapping("/{clientId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Client> blockProfile(@PathVariable Long clientId) {
        Client blockedClient = serviceClient.blockProfile(clientId);
        return new ResponseEntity<>(blockedClient, HttpStatus.OK);
    }
}
