package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateCertificationDto;
import com.talentcloud.profile.iservice.IServiceCertification;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Certification;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for service methods

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
    @Transactional(readOnly = true) // Good practice for read-only operations
    public Optional<Certification> getCertificationById(Long certificationId) {
        return certificationRepository.findById(certificationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certification> getAllCertificationsByCandidateId(Long candidateId) {
        // This logic is clean and correct.
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
        return certificationRepository.findByCandidate(candidate);
    }

    @Override
    @Transactional
    public Certification addCertification(Certification certification, Long candidateId) {
        // Find the candidate and associate it. Spring Auditing handles the timestamps.
        return candidateRepository.findById(candidateId)
                .map(candidate -> {
                    certification.setCandidate(candidate);
                    return certificationRepository.save(certification);
                })
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));
    }

    @Override
    @Transactional
    public Certification updateCertification(Long certificationId, UpdateCertificationDto dto) {
        // Find the entity, update its fields. Spring Auditing handles the updated_at timestamp.
        Certification existingCertification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("Certification not found with id: " + certificationId));

        existingCertification.setName(dto.getName());
        existingCertification.setOrganization(dto.getOrganization());
        existingCertification.setObtainedDate(dto.getObtainedDate());
        existingCertification.setCertificationUrl(dto.getCertificationUrl());

        return certificationRepository.save(existingCertification);
    }

    @Override
    @Transactional
    public void deleteCertification(Long certificationId) {
        // Ensure the certification exists before attempting to delete.
        if (!certificationRepository.existsById(certificationId)) {
            throw new IllegalArgumentException("Certification not found with id: " + certificationId);
        }
        certificationRepository.deleteById(certificationId);
    }
}