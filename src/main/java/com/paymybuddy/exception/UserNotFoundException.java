package com.paymybuddy.exception;

public class UserNotFoundException extends RuntimeException {
    private final String redirectUrl;

    public UserNotFoundException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
