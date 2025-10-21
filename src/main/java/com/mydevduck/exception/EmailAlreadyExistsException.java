package com.mydevduck.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) { super(message); }

    public EmailAlreadyExistsException(String resource, String field, Object value) {

    }
}
