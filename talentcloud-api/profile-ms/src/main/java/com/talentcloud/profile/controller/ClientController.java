//package com.talentcloud.profile.controller;
//
//import com.talentcloud.profile.dto.CandidatePublicProfileDto;
//import com.talentcloud.profile.dto.CreateClientProfileDto;
//import com.talentcloud.profile.dto.UpdateClientProfessionalDto; // UPDATED
//import com.talentcloud.profile.iservice.IServiceCandidate;
//import com.talentcloud.profile.iservice.IServiceClient;
//import com.talentcloud.profile.model.Client;
//import com.talentcloud.profile.model.Profile;
//import com.talentcloud.profile.service.ProfileService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/client")
//public class ClientController {
//
//    private final IServiceClient clientService;
//    private final IServiceCandidate candidateService;
//    private final ProfileService profileService;
//
//    @Autowired
//    public ClientController(IServiceClient serviceClient, IServiceCandidate candidateService, ProfileService profileService) {
//        this.clientService = serviceClient;
//        this.candidateService = candidateService;
//        this.profileService = profileService;
//    }
//
//    // STEP 2 (CREATE): Creates the professional profile part.
//    @PostMapping("/me")
//    public ResponseEntity<?> createMyClientProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateClientProfileDto dto) {
//        Profile userProfile = profileService.findOrCreateProfile(
//                jwt.getClaimAsString("sub"),
//                jwt.getClaimAsString("email"),
//                jwt.getClaimAsString("given_name"),
//                jwt.getClaimAsString("family_name")
//        );
//
//        if (clientService.getClientProfileByProfileUserId(userProfile.getId()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Client profile already exists for this user.");
//        }
//
//        Client client = new Client();
//        client.setCompanyName(dto.getCompanyName());
//        client.setIndustry(dto.getIndustry());
//        client.setCountry(dto.getCountry());
//        client.setWebsite(dto.getWebsite());
//        client.setLinkedInUrl(dto.getLinkedInUrl());
//        client.setCompanyDescription(dto.getCompanyDescription());
//
//        Client createdClient = clientService.createClientProfile(client, userProfile);
//        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
//    }
//
//    // STEP 2 (UPDATE): Updates ONLY the professional profile part.
//    @PutMapping("/me")
//    public ResponseEntity<Client> updateMyProfessionalDetails(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpdateClientProfessionalDto dto) {
//        String authServiceUserId = jwt.getClaimAsString("sub");
//
//        Client updatedClient = clientService.updateClientProfessionalDetails(authServiceUserId, dto);
//        return ResponseEntity.ok(updatedClient);
//    }
//
//    @GetMapping("/candidates")
//    public ResponseEntity<List<CandidatePublicProfileDto>> getAllCandidates() {
//        List<CandidatePublicProfileDto> candidates = candidateService.getAllCandidatesAsPublicProfile();
//        return ResponseEntity.ok(candidates);
//    }
//
//    @GetMapping("/candidates/{candidateId}")
//    public ResponseEntity<CandidatePublicProfileDto> getCandidateById(@PathVariable("candidateId") Long candidateId) {
//        return candidateService.getCandidateByIdAsPublicProfile(candidateId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//}
