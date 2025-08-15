package com.voriq.security_service.exception_handler.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RestException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException( String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
