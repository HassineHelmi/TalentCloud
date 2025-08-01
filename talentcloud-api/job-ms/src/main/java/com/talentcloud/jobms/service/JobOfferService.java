package com.talentcloud.jobms.service;


import com.talentcloud.jobms.dto.JobOfferDTO;
import com.talentcloud.jobms.exception.ApplicationNotFoundException;
import com.talentcloud.jobms.model.Application;
import com.talentcloud.jobms.model.ApplicationStatus;
import com.talentcloud.jobms.model.JobOffer;
import com.talentcloud.jobms.model.OfferStatus;
import com.talentcloud.jobms.repository.ApplicationRepository;
import com.talentcloud.jobms.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;

    @Transactional
    public JobOfferDTO sendOffer(JobOfferDTO dto) {
        Application app = applicationRepository.findById(dto.applicationId())
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + dto.applicationId()));

        JobOffer offer = new JobOffer();
        offer.setApplication(app);
        offer.setOfferDetails(dto.offerDetails());
        offer.setStatus(OfferStatus.PENDING);
        offer.setSentAt(LocalDateTime.now());
        offer.setExpiresAt(dto.expiresAt());

        JobOffer savedOffer = jobOfferRepository.save(offer);

        // Update application status
        applicationService.updateApplicationStatus(app.getId(), ApplicationStatus.OFFER_EXTENDED);

        return JobOfferDTO.fromEntity(savedOffer);
    }

    @Transactional
    public JobOfferDTO respondToOffer(Long offerId, OfferStatus response) {
        if (response != OfferStatus.ACCEPTED && response != OfferStatus.REJECTED) {
            throw new IllegalArgumentException("Response must be ACCEPTED or REJECTED.");
        }

        JobOffer offer = jobOfferRepository.findById(offerId)
                .orElseThrow(() -> new ApplicationNotFoundException("Offer not found with id: " + offerId));

        offer.setStatus(response);
        JobOffer updatedOffer = jobOfferRepository.save(offer);

        // Update application status accordingly
        ApplicationStatus newStatus = (response == OfferStatus.ACCEPTED) ? ApplicationStatus.HIRED : ApplicationStatus.REJECTED;
        applicationService.updateApplicationStatus(offer.getApplication().getId(), newStatus);

        return JobOfferDTO.fromEntity(updatedOffer);
    }
}