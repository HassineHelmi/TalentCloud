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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMyAdminProfile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        Optional<Admin> admin = adminService.getAdminProfileByUserId(userId);
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.notFound().build(); // Admin profile not found for this user ID
        }
    }

    // This endpoint might be used for initial setup or by another admin to create one
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')") // Only existing ADMINs can create new admin profiles
    public ResponseEntity<Admin> createAdminProfile(@RequestBody Admin admin, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        // Ensure the admin being created is for the authenticated user, or that the current user has rights to create it.
        // For simplicity, we'll enforce that the incoming admin.userId matches the authenticated user.
        // In a real scenario, this might be more complex (e.g., an admin creating another admin's profile).
        if (!userId.equals(admin.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Admin createdAdmin = adminService.createAdminProfile(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    // You can add other admin-specific endpoints here (e.g., view all users, manage roles etc.)
}