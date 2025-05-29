package com.talentcloud.profile.controller;

import com.talentcloud.profile.iservice.IServiceAdmin;
import com.talentcloud.profile.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final IServiceAdmin adminService;

    @Autowired
    public AdminController(IServiceAdmin adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMyAdminProfile(Authentication authentication) {
        Long profileUserId = Long.valueOf(authentication.getName());
        Optional<Admin> admin = adminService.getAdminProfileByProfileUserId(profileUserId);
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Admin> createAdminProfile(@RequestBody Admin admin, Authentication authentication) {
        Long authenticatedProfileUserId = Long.valueOf(authentication.getName());
        // Ensure the admin profile being created is for the authenticated user.
        // Update getter call here
        if (admin.getProfileUserId() == null || !admin.getProfileUserId().equals(authenticatedProfileUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Admin createdAdmin = adminService.createAdminProfile(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }
}