package com.talentcloud.auth.exception;

public class KeycloakUserParsingException extends RuntimeException {
    public KeycloakUserParsingException(String message) {
        super(message);
    }
}
