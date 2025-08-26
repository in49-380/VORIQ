package com.voriq.car_catalog_service.exception_handler.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException( String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }
}
