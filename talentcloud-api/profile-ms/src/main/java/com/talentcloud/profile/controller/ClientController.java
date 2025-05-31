package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.dto.ErrorResponse;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.service.ProfileService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final IServiceClient serviceClient;
    private final ProfileService profileService;

    @Autowired
    public ClientController(IServiceClient serviceClient, ProfileService profileService) {
        this.serviceClient = serviceClient;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> getMyClientProfile(@AuthenticationPrincipal Jwt jwt) {
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");

        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email);
        Optional<Client> client = serviceClient.getClientProfileByProfileUserId(userProfile.getId());

        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Client-specific details not found for user " + jwtSub + ". Profile may exist but not as a client.",
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> createClientProfile(@RequestBody @Valid Client clientRequest, @AuthenticationPrincipal Jwt jwt) {
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");

        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email);
        Long profileTableId = userProfile.getId();

        // Check if a client profile already exists for this profile_id
        if (serviceClient.getClientProfileByProfileUserId(profileTableId).isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Client profile already exists for this user.",
                    "Conflict",
                    LocalDateTime.now(),
                    HttpStatus.CONFLICT.value()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }


        clientRequest.setProfileUserId(profileTableId);

        clientRequest.setId(null); // Let database generate ID
        clientRequest.setBlocked(false); // Default value for new clients


        try {
            Client createdClient = serviceClient.createClientProfile(clientRequest);
            return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Failed to save client profile due to data integrity issues: " + e.getMessage(),
                    "Bad Request",
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // Log the exception e for detailed debugging
            ErrorResponse errorResponse = new ErrorResponse(
                    "An unexpected error occurred while creating the client profile: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    public ResponseEntity<?> getClientById(@PathVariable Long clientId, @AuthenticationPrincipal Jwt jwt) {
        Optional<Client> optionalClient = serviceClient.getClientById(clientId);

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            String currentJwtSub = jwt.getClaimAsString("sub");
            String currentEmail = jwt.getClaimAsString("email");

            Profile requestUserProfile = profileService.findOrCreateProfile(currentJwtSub, currentEmail);
            boolean isOwner = requestUserProfile.getId().equals(client.getProfileUserId());

            @SuppressWarnings("unchecked")
            List<String> realmRoles = jwt.getClaimAsMap("realm_access") != null ?
                    (List<String>) jwt.getClaimAsMap("realm_access").get("roles") :
                    List.of();
            Set<String> roles = realmRoles.stream().collect(Collectors.toSet());


            if (isOwner || roles.contains("ROLE_ADMIN")) {
                return ResponseEntity.ok(client);
            } else {
                ErrorResponse errorResponse = new ErrorResponse(
                        "Access denied to this client profile.",
                        "Forbidden",
                        LocalDateTime.now(),
                        HttpStatus.FORBIDDEN.value()
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Client not found with id " + clientId,
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> editClientProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpdateClientDto dto) {
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email);

        Optional<Client> optionalClient = serviceClient.getClientProfileByProfileUserId(userProfile.getId());

        if (optionalClient.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Client profile not found for authenticated user. Cannot edit.",
                    "Not Found",
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        Long clientPrimaryKey = optionalClient.get().getId(); // This is clients.id

        try {
            Client updatedClient = serviceClient.updateClientProfile(clientPrimaryKey, dto);
            return ResponseEntity.ok(updatedClient);
        } catch (Exception e) { // Catch more specific exceptions if possible
            // Log the exception e
            ErrorResponse errorResponse = new ErrorResponse(
                    "Error updating client profile: " + e.getMessage(),
                    "Internal Server Error",
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = serviceClient.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PutMapping("/{clientId}/block")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> blockClientProfile(@PathVariable Long clientId) { // Renamed for clarity
        try {
            Client blockedClient = serviceClient.blockProfile(clientId);
            return ResponseEntity.ok(blockedClient);
        } catch (RuntimeException e) { // Example: Catch a specific exception if service throws one for not found
            ErrorResponse errorResponse = new ErrorResponse(
                    "Error blocking client: " + e.getMessage(),
                    e.getMessage().contains("not found") ? "Not Found" : "Internal Server Error", // Basic error type detection
                    LocalDateTime.now(),
                    e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND.value() : HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }
}