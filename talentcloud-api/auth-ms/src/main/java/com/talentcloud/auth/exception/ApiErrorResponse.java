package com.talentcloud.auth.exception;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;
    private String path;
    private Map<String, String> messages;
}
