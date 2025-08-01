package com.talentcloud.jobms.dto;

import com.talentcloud.jobms.model.JobOffer;
import com.talentcloud.jobms.model.OfferStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record JobOfferDTO(
        Long id,
        @NotNull Long applicationId,
        OfferStatus status,
        String offerDetails,
        LocalDateTime sentAt,
        LocalDateTime expiresAt
) {
    /**
     * Converts a JobOffer entity to a JobOfferDTO.
     * @param offer The JobOffer entity.
     * @return A new JobOfferDTO instance.
     */
    public static JobOfferDTO fromEntity(JobOffer offer) {
        return new JobOfferDTO(
                offer.getId(),
                offer.getApplication().getId(),
                offer.getStatus(),
                offer.getOfferDetails(),
                offer.getSentAt(),
                offer.getExpiresAt()
        );
    }
}