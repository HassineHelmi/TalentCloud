package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CandidatePublicProfileDto;
import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.exception.CandidateNotFoundException;
import com.talentcloud.profile.iservice.IServiceCandidate;
import com.talentcloud.profile.model.Candidate;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandidateService implements IServiceCandidate {

    private final CandidateRepository candidateRepository;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, ProfileRepository profileRepository, S3Service s3Service) {
        this.candidateRepository = candidateRepository;
        this.profileRepository = profileRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidatePublicProfileDto> getAllCandidatesAsPublicProfile() {
        List<Object[]> results = candidateRepository.findAllCandidatesWithProfiles();
        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(result -> {
                    Candidate candidate = (Candidate) result[0];
                    Profile profile = (Profile) result[1];
                    return convertToPublicProfileDto(candidate, profile);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CandidatePublicProfileDto> getCandidateByIdAsPublicProfile(Long candidateId) {
        return candidateRepository.findById(candidateId).map(candidate -> {
            Profile profile = profileRepository.findById(candidate.getProfileUserId()).orElse(null);
            return convertToPublicProfileDto(candidate, profile);
        });
    }

    private CandidatePublicProfileDto convertToPublicProfileDto(Candidate candidate, Profile profile) {
        CandidatePublicProfileDto dto = new CandidatePublicProfileDto();

        dto.setId(candidate.getId());

        // Generate fresh presigned URL for resume if stored URL looks like an S3 key
        String resumeUrl = candidate.getResumeUrl();
        if (resumeUrl != null && !resumeUrl.startsWith("http")) {
            // This looks like an S3 key, generate a fresh presigned URL
            try {
                resumeUrl = s3Service.generatePresignedUrl(bucketName, resumeUrl);
            } catch (Exception e) {
                // Log error but keep original URL as fallback
                System.err.println("Failed to generate presigned URL for resume: " + e.getMessage());
            }
        }
        dto.setResumeUrl(resumeUrl);

        dto.setJobTitle(candidate.getJobTitle());
        dto.setJobCategory(candidate.getJobCategory());
        dto.setVisibilitySettings(candidate.getVisibilitySettings());
        dto.setBlocked(candidate.isBlocked());

        // Convert Set to List for each collection
        if (candidate.getEducations() != null) {
            dto.setEducations(new ArrayList<>(candidate.getEducations()));
        }
        if (candidate.getExperiences() != null) {
            dto.setExperiences(new ArrayList<>(candidate.getExperiences()));
        }
        if (candidate.getCertifications() != null) {
            dto.setCertifications(new ArrayList<>(candidate.getCertifications()));
        }
        if (candidate.getSkills() != null) {
            dto.setSkills(new ArrayList<>(candidate.getSkills()));
        }

        if (profile != null) {
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setEmail(profile.getEmail());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setAddress(profile.getAddress());
        }

        return dto;
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

        if (dto.getResumeUrl() != null) {
            existingCandidate.setResumeUrl(dto.getResumeUrl());
        }
        if (dto.getJobTitle() != null) {
            existingCandidate.setJobTitle(dto.getJobTitle());
        }
        if (dto.getJobCategory() != null) {
            existingCandidate.setJobCategory(dto.getJobCategory());
        }
        if (dto.getVisibilitySettings() != null) {
            existingCandidate.setVisibilitySettings(dto.getVisibilitySettings());
        }

        return candidateRepository.save(existingCandidate);
    }
}