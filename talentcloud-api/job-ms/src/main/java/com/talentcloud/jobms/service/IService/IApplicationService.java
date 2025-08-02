package com.talentcloud.jobms.service.IService;

import com.talentcloud.jobms.dto.ApplicationDTO;
import com.talentcloud.jobms.dto.JobOfferDTO;
import com.talentcloud.jobms.model.Application;
import com.talentcloud.jobms.model.ApplicationStatus;

import java.util.List;

public interface IApplicationService {
    Application submitApplication(ApplicationDTO applicationDto);
    List<Application> getApplicationsForJob(Long jobId);
    Application updateApplicationStatus(Long applicationId, ApplicationStatus newStatus);
    JobOfferDTO createJobOffer(JobOfferDTO jobOfferDto);
    void respondToJobOffer(Long offerId, boolean accepted);
}