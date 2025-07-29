package com.talentcloud.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentcloud.profile.dto.CvParsedDataDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test")
public class SqsTestController {

    private static final Logger logger = LoggerFactory.getLogger(SqsTestController.class);

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.name}")
    private String queueName;

    public SqsTestController(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sqs-send")
    public ResponseEntity<String> sendTestSqsMessage() {
        // 1. Create a sample test message
        CvParsedDataDto testMessage = createTestMessage();
        logger.info("--- [SQS TEST] Creating test message for queue: {}", queueName);

        try {
            // 2. Convert the Java object to a JSON string
            String messagePayload = objectMapper.writeValueAsString(testMessage);

            // 3. Send the message to the queue
            sqsTemplate.send(to -> to.queue(queueName).payload(messagePayload));

            logger.info("--- [SQS TEST] Successfully sent test message to SQS. Check the application logs for the receiving message.");

            return ResponseEntity.ok("Test message sent to SQS queue: " + queueName);

        } catch (JsonProcessingException e) {
            logger.error("--- [SQS TEST] Failed to serialize test message to JSON", e);
            return ResponseEntity.internalServerError().body("Failed to serialize test message.");
        } catch (Exception e) {
            logger.error("--- [SQS TEST] Failed to send message to SQS", e);
            return ResponseEntity.internalServerError().body("Failed to send message: " + e.getMessage());
        }
    }

    private CvParsedDataDto createTestMessage() {
        CvParsedDataDto dto = new CvParsedDataDto();
        dto.setAuthUserId("test-user-" + UUID.randomUUID().toString());
        dto.setResumeKey("test/resume/key.pdf");
        dto.setFirstName("TestFirstName");
        dto.setLastName("TestLastName");
        dto.setEmail("test.user@example.com");
        dto.setCurrentJob("SQS Test Engineer");

        CvParsedDataDto.SkillsDto skills = new CvParsedDataDto.SkillsDto();
        skills.setProgrammingLanguages(List.of("Java", "SQL"));
        dto.setSkills(skills);

        return dto;
    }
}