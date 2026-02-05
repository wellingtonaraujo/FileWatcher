package com.wellington.easyeyeintegrator.dto;

public class AuthorizationResult {

    private final boolean authorized;
    private final String reason;

    public AuthorizationResult(boolean authorized, String reason) {
        this.authorized = authorized;
        this.reason = reason;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getReason() {
        return reason;
    }
}
