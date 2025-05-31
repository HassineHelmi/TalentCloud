package com.talentcloud.profile.controller;

import com.talentcloud.profile.dto.ErrorResponse; // Assuming you have this
import com.talentcloud.profile.iservice.IServiceAdmin;
import com.talentcloud.profile.model.Admin;
import com.talentcloud.profile.model.Profile;     // Import Profile
import com.talentcloud.profile.service.ProfileService; // Import ProfileService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import
import org.springframework.security.oauth2.jwt.Jwt; // Import
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // For ErrorResponse
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final IServiceAdmin adminService;
    private final ProfileService profileService; // Inject ProfileService

    @Autowired
    public AdminController(IServiceAdmin adminService, ProfileService profileService) {
        this.adminService = adminService;
        this.profileService = profileService; // Initialize ProfileService
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMyAdminProfile(@AuthenticationPrincipal Jwt jwt) { // Changed parameter
        String jwtSub = jwt.getClaimAsString("sub");
        // Assuming your ProfileService might use email, if not, you might not need it here
        String email = jwt.getClaimAsString("email");
        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email); // Use ProfileService

        if (userProfile == null || userProfile.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unable to retrieve profile information.", "Server Error", LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        Optional<Admin> admin = adminService.getAdminProfileByProfileUserId(userProfile.getId());
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Admin profile not found for this user.", "Not Found", LocalDateTime.now(), HttpStatus.NOT_FOUND.value()));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // RequestBody can be optional if Admin has no other fields than id and profileUserId
    public ResponseEntity<?> createAdminProfile(@RequestBody(required = false) Admin adminRequestFromUser, @AuthenticationPrincipal Jwt jwt) { // Changed parameter
        String jwtSub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        Profile userProfile = profileService.findOrCreateProfile(jwtSub, email); // Use ProfileService
        Long authenticatedProfileId = userProfile.getId();

        if (authenticatedProfileId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unable to retrieve profile information for admin creation.", "Server Error", LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        // Check if an admin profile already exists for this user
        if (adminService.getAdminProfileByProfileUserId(authenticatedProfileId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Admin profile already exists for this user.", "Conflict", LocalDateTime.now(), HttpStatus.CONFLICT.value()));
        }

        Admin newAdmin = new Admin();
        newAdmin.setProfileUserId(authenticatedProfileId);
        // If your Admin entity had other fields to be set from adminRequestFromUser, you'd map them here.
        // Since it only has id and profileUserId, we primarily use the authenticatedProfileId.

        try {
            Admin createdAdmin = adminService.createAdminProfile(newAdmin);
            return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating admin profile: " + e.getMessage(), "Server Error", LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}