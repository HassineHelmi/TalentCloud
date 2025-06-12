package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.CandidatePublicProfileDto;
import com.talentcloud.profile.dto.ClientProfileDto;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@PreAuthorize("hasAuthority('ROLE_CLIENT')")
public class ClientController {

    private final IServiceClient clientService;
    private final IServiceCandidate candidateService;
    private final ProfileService profileService;

    @Autowired
    public ClientController(IServiceClient serviceClient, IServiceCandidate candidateService, ProfileService profileService) {
        this.clientService = serviceClient;
        this.candidateService = candidateService;
        this.profileService = profileService;
    }

    @PostMapping("/me")
    public ResponseEntity<?> createMyClientProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ClientProfileDto dto) {
        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), dto.getFirstName(), dto.getLastName());

        if (clientService.getClientProfileByProfileUserId(userProfile.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Client profile already exists for this user.");
        }

        Client client = new Client();
        client.setCompanyName(dto.getCompanyName());
        client.setIndustry(dto.getIndustry());
        client.setCountry(dto.getCountry());
        client.setWebsite(dto.getWebsite());
        client.setLinkedInUrl(dto.getLinkedInUrl());
        client.setCompanyDescription(dto.getCompanyDescription());

        Client createdClient = clientService.createClientProfile(client, userProfile);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @PutMapping("/me")
    public ResponseEntity<Client> editMyClientDetails(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ClientProfileDto dto) {
        Profile userProfile = profileService.findOrCreateProfile(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), dto.getFirstName(), dto.getLastName());

        Client client = clientService.getClientProfileByProfileUserId(userProfile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client profile not found for authenticated user."));

        Client updatedClient = clientService.updateClientAndProfile(client.getId(), dto);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidatePublicProfileDto>> getAllCandidates() {
        List<CandidatePublicProfileDto> candidates = candidateService.getAllCandidatesAsPublicProfile();
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<CandidatePublicProfileDto> getCandidateById(@PathVariable ("candidateId") Long candidateId) {
        return candidateService.getCandidateByIdAsPublicProfile(candidateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}