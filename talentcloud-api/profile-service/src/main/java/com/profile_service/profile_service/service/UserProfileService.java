//package com.profile_service.profile_service.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import java.util.List;
//import java.util.Optional;
//
//import com.profile_service.profile_service.model.UserProfile;
//import com.profile_service.profile_service.repository.UserProfileRepository;
//
//@Service
//public class UserProfileService {
//
//    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
//
//    @Autowired
//    private UserProfileRepository repository;
//
//    // ✅ Récupérer tous les utilisateurs
//    public List<UserProfile> getAllUsers() {
//        return repository.findAll();
//    }
//
//    // ✅ Récupérer un utilisateur par ID
//    public Optional<UserProfile> getUserProfile(Long id) {
//        Optional<UserProfile> profile = repository.findById(id);
//        if (profile.isPresent()) {
//            logger.info("Profil trouvé : Email = {}", profile.get().getEmail());
//        } else {
//            logger.warn("Aucun profil trouvé pour l'ID : {}", id);
//        }
//        return profile;
//    }
//
//    // ✅ Sauvegarder un utilisateur
//    public UserProfile saveUserProfile(UserProfile user) {
//        logger.info("Sauvegarde de l'utilisateur : {}", user.getEmail());
//        return repository.save(user);
//    }
//}
