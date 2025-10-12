package com.mydevduck.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException() {
        super("External service error");
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(String service, String message) {
        super(String.format("%s service error: %s", service, message));
    }

}
