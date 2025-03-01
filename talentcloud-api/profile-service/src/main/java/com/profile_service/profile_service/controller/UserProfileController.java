package com.profile_service.profile_service.controller;



import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.profile_service.profile_service.model.UserProfile;
import com.profile_service.profile_service.service.UserProfileService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService service;

    // ✅ Récupérer tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllUsers() {
        logger.info("Requête GET pour récupérer tous les utilisateurs");
        List<UserProfile> users = service.getAllUsers();
        
        if (users.isEmpty()) {
            logger.warn("Aucun utilisateur trouvé dans la base de données.");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    // ✅ Récupérer un utilisateur par ID
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long id) {
        logger.info("Requête GET pour ID : {}", id);
        Optional<UserProfile> profile = service.getUserProfile(id);
        
        return profile.map(user -> {
            logger.info("Utilisateur trouvé : ID = {}, Email = {}", user.getId(), user.getEmail());
            return ResponseEntity.ok(user);
        }).orElseGet(() -> {
            logger.warn("Aucun utilisateur trouvé avec ID {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // ✅ Ajouter un utilisateur (POST)
    @PostMapping
    public ResponseEntity<UserProfile> createUser(@RequestBody UserProfile user) {
        try {
            logger.info("Requête POST pour créer un nouvel utilisateur : {}", user.getEmail());
            UserProfile createdUser = service.saveUserProfile(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
