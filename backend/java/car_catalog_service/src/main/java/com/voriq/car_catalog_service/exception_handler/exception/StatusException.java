package com.voriq.car_catalog_service.exception_handler.exception;

import org.springframework.http.HttpStatus;

public class StatusException extends RestException {
    public StatusException(String message) {
        super(HttpStatus.METHOD_NOT_ALLOWED, message);
    }
}
