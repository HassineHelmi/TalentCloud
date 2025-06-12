package com.talentcloud.profile.controller;

import com.talentcloud.profile.iservice.IServiceAdmin;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Admin;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin/manage")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final IServiceAdmin adminService;
    private final IServiceClient clientService;
    private final IServiceCandidate candidateService;

    @Autowired
    public AdminController(IServiceAdmin adminService, IServiceClient clientService, IServiceCandidate candidateService) {
        this.adminService = adminService;
        this.clientService = clientService;
        this.candidateService = candidateService;
    }

    // Admin Management
    @PostMapping("/admins")
    public ResponseEntity<?> createAdminProfileForUser(@RequestParam Long profileId) {
        if (adminService.getAdminProfileByProfileUserId(profileId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin profile already exists for this user.");
        }
        Admin newAdmin = new Admin();
        newAdmin.setProfileUserId(profileId);
        Admin createdAdmin = adminService.createAdminProfile(newAdmin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    // Client Management
    @GetMapping("/clients")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId) {
        return clientService.getClientById(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/clients/{clientId}/block")
    public ResponseEntity<Client> blockClientProfile(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.blockProfile(clientId));
    }

    // Candidate Management
    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long candidateId) {
        return candidateService.getCandidateById(candidateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/candidates/{candidateId}/block")
    public ResponseEntity<Candidate> blockCandidateProfile(@PathVariable Long candidateId) {
        return ResponseEntity.ok(candidateService.blockProfile(candidateId));
    }
}
