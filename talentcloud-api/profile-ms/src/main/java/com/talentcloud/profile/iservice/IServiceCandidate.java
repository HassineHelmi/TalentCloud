package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateCandidateDto;
import com.talentcloud.profile.exception.CandidateNotFoundException;
import com.talentcloud.profile.model.Candidate;

import java.util.List;
import java.util.Optional;

public interface IServiceCandidate {
        Candidate createCandidateProfile(Candidate candidate);
        Candidate blockProfile(Long candidateId) throws CandidateNotFoundException;
        Candidate editCandidateProfile(Long candidateId, UpdateCandidateDto dto) throws CandidateNotFoundException;
        Optional<Candidate> getCandidateById(Long candidateId);
        List<Candidate> getAllCandidates();
        Optional<Candidate> getCandidateProfileByProfileUserId(Long profileUserId);

}
