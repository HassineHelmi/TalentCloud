package com.talentcloud.interview_ms.kafka.producer;

import com.talentcloud.interview_ms.kafka.event.InterviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewEventProducer {

    private final KafkaTemplate<String, InterviewCreatedEvent> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(InterviewEventProducer.class);

    private static final String TOPIC = "interview-events";

    public void send(InterviewCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        log.info("Sent event: {}", event);
    }
}
