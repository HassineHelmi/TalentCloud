package com.talentcloud.profile.service;
import com.talentcloud.profile.dto.UpdateCandidateDto;

import com.talentcloud.profile.exception.CandidateNotFoundException;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.repository.CandidateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService implements IServiceCandidate {

    private final CandidateRepository candidateRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Candidate createCandidateProfile(Candidate candidate) {
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        return candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public Candidate blockProfile(Long candidateId) throws Exception {
        Candidate existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with id " + candidateId));

        existingCandidate.setBlocked(true);
        existingCandidate.setUpdatedAt(LocalDateTime.now());
        return candidateRepository.save(existingCandidate);
    }

    @Override
    public Optional<Candidate> getCandidateById(Long candidateId) {
        return candidateRepository.findById(candidateId);  // Find by ID
    }

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();  // Get all candidates
    }
    @Override
    @Transactional
    public Candidate editCandidateProfile(Long candidateId, UpdateCandidateDto dto) throws Exception {
        Candidate existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with id " + candidateId));

        if (dto.getProfilePicture() != null) existingCandidate.setProfilePicture(dto.getProfilePicture());
        if (dto.getResume() != null) existingCandidate.setResume(dto.getResume());
        if (dto.getJobPreferences() != null) existingCandidate.setJobPreferences(dto.getJobPreferences());
        if (dto.getJobTitle() != null) existingCandidate.setJobTitle(dto.getJobTitle());

        existingCandidate.setUpdatedAt(LocalDateTime.now());

        return candidateRepository.save(existingCandidate);
    }
}