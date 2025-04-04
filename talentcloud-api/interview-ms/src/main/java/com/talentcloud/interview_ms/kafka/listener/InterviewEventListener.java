package com.talentcloud.interview_ms.kafka.listener;

import com.talentcloud.interview_ms.kafka.event.InterviewCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InterviewEventListener {

    private final Logger log = LoggerFactory.getLogger(InterviewEventListener.class);

    @KafkaListener(topics = "interview-events", groupId = "interview-ms")
    public void listen(InterviewCreatedEvent event) {
        log.info("Received event: {}", event);
    }
}
