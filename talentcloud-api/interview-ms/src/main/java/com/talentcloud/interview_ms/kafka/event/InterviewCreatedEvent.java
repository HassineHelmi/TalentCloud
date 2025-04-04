package com.talentcloud.interview_ms.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCreatedEvent {
    private String interviewId;
    private String userId;
    private String jobTitle;
    private String timestamp;
}
