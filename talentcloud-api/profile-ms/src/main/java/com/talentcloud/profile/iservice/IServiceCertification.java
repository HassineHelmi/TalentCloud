package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateCertificationDto;
import com.talentcloud.profile.model.Certification;

import java.util.List;
import java.util.Optional;

public interface IServiceCertification {
    Optional<Certification> getCertificationById(Long certificationId);

    List<Certification> getAllCertificationsByCandidateId(Long candidateId);

    Certification addCertification(Certification certification, Long candidateId);

    Certification updateCertification(Long certificationId, UpdateCertificationDto dto);

    void deleteCertification(Long certificationId);
}
