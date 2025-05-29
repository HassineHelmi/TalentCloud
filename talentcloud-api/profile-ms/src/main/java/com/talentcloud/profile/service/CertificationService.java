package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateCertificationDto;
import com.talentcloud.profile.iservice.IServiceCertification;
import com.talentcloud.profile.model.Certification;
import com.talentcloud.profile.repository.CertificationRepository;
import com.talentcloud.profile.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CertificationService implements IServiceCertification {

    private final CertificationRepository certificationRepository;
    private final CandidateRepository candidateRepository;

    @Autowired
    public CertificationService(CertificationRepository certificationRepository, CandidateRepository candidateRepository) {
        this.certificationRepository = certificationRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Optional<Certification> getCertificationById(Long certificationId) {
        return certificationRepository.findById(certificationId);
    }

    @Override
    public List<Certification> getAllCertificationsByCandidateId(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(certificationRepository::findByCandidate)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
    }

    @Override
    public Certification addCertification(Certification certification, Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(candidate -> {
                    certification.setCandidate(candidate);
                    certification.setCreatedAt(LocalDateTime.now());
                    certification.setUpdatedAt(LocalDateTime.now());
                    return certificationRepository.save(certification);
                })
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
    }

    @Override
    public Certification updateCertification(Long certificationId, UpdateCertificationDto dto) {
        Certification existingCertification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("Certification not found with id: " + certificationId));
        // Update the certification with data from the DTO
        existingCertification.setNom(dto.getNom());
        existingCertification.setOrganisme(dto.getOrganisme());
        existingCertification.setDateObtention(dto.getDateObtention());
        // Removed existingCertification.setDatevalidite(dto.getDatevalidite());
        existingCertification.setCertificationUrl(dto.getCertificationUrl()); // Updated field

        // Set the updatedAt timestamp to the current time
        existingCertification.setUpdatedAt(LocalDateTime.now());
        // Save and return the updated certification
        return certificationRepository.save(existingCertification);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        Certification existingCertification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("Certification not found with id: " + certificationId));
        certificationRepository.delete(existingCertification);
    }
}