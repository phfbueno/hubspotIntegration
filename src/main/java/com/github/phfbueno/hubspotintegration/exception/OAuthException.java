package com.github.phfbueno.hubspotintegration.exception;

public class OAuthException extends RuntimeException {

    private String errorCode;


    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OAuthException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

