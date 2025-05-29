package com.talentcloud.profile.service;
import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.exception.CandidateNotFoundException;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Candidate createCandidateProfile(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public Candidate blockProfile(Long candidateId) throws CandidateNotFoundException {
        Candidate existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with id " + candidateId));
        existingCandidate.setBlocked(true);
        return candidateRepository.save(existingCandidate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Candidate> getCandidateById(Long candidateId) {
        return candidateRepository.findById(candidateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Candidate> getCandidateProfileByProfileUserId(Long profileUserId) {
        return candidateRepository.findByProfileUserId(profileUserId);
    }

    @Override
    @Transactional
    public Candidate editCandidateProfile(Long candidateId, UpdateCandidateDto dto) throws CandidateNotFoundException {
        Candidate existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with id " + candidateId));

        // Update fields if DTO provides new values
        if (dto.getResume_url() != null) {
            existingCandidate.setResume_url(dto.getResume_url());
        }
        if (dto.getJobPreference() != null) {
            existingCandidate.setJobPreference(dto.getJobPreference());
        }
        if (dto.getVisibilitySettings() != null) {
            existingCandidate.setVisibilitySetting(dto.getVisibilitySettings());
        }

        return candidateRepository.save(existingCandidate);
    }
}