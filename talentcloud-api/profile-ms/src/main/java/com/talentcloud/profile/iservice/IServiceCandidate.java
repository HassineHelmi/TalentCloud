package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.model.Candidate;

import java.util.List;
import java.util.Optional;

public interface IServiceCandidate {
        Candidate createCandidateProfile(Candidate candidate);
        Candidate blockProfile(Long candidateId) throws Exception;
        Candidate editCandidateProfile(Long candidateId, UpdateCandidateDto dto) throws Exception;
        Optional<Candidate> getCandidateById(Long candidateId);
        List<Candidate> getAllCandidates();
        Optional<Candidate> getCandidateProfileByUserId(Long userId);
    }