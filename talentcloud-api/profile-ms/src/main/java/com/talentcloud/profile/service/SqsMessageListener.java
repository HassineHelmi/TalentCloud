package com.talentcloud.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentcloud.profile.dto.CvParsedDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.annotation.SqsListener;

import java.util.Map;

@Service
public class SqsMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SqsMessageListener.class);
    private final SqsProfileService sqsProfileService;
    private final ObjectMapper objectMapper;

    public SqsMessageListener(SqsProfileService sqsProfileService, ObjectMapper objectMapper) {
        this.sqsProfileService = sqsProfileService;
        this.objectMapper = objectMapper;
    }

    // This listener is ALWAYS active in the background
    @SqsListener("cv-processing-destination-queue")
    public void receiveMessage(@Payload CvParsedDataDto profileData, @Headers Map<String, Object> headers) {
        try {
            // Log the raw message by converting the Java object back to a JSON string
            String rawJson = objectMapper.writeValueAsString(profileData);

            // UNCOMMENT THIS LINE to see the full JSON message in your logs
            logger.info("Raw SQS message received: {}", rawJson);

            // Log important fields from the message
            logger.info("Received candidate profile - AuthUserId: {}, Name: {} {}, Email: {}, ResumeKey: {}",
                    profileData.getAuthUserId(),
                    profileData.getFirstName(),
                    profileData.getLastName(),
                    profileData.getEmail(),
                    profileData.getResumeKey());

            // Process the message
            sqsProfileService.processProfileData(profileData);

            logger.info("Successfully processed and saved profile for {}",
                    profileData.getEmail() != null ? profileData.getEmail() : profileData.getAuthUserId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message for logging: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing SQS message: {}", e.getMessage(), e);
        }
    }
}