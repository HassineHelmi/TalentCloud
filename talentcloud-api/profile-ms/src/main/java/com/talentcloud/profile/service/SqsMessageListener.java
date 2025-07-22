package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CvParsedDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class SqsMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SqsMessageListener.class);
    private final SqsProfileService sqsProfileService;

    public SqsMessageListener(SqsProfileService sqsProfileService) {
        this.sqsProfileService = sqsProfileService;
    }

    @SqsListener("${sqs.queue.name}")
    public void receiveMessage(CvParsedDataDto profileData) {
        logger.info("Received candidate profile for: {} {}", profileData.getFirstName(), profileData.getLastName());

        try {
            // Create or update profile based on email
            sqsProfileService.processProfileData(profileData);
            
            logger.info("Successfully processed and saved profile for {}", profileData.getEmail());
        } catch (Exception e) {
            logger.error("Error processing SQS message: {}", e.getMessage(), e);
        }
    }
}
