package com.talentcloud.auth.exception;

public class KeycloakRoleNotFoundException extends RuntimeException {
    public KeycloakRoleNotFoundException(String message) {
        super(message);
    }
}
