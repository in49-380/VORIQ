package com.voriq.car_catalog_service.exception_handler.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenAuthenticationException extends AuthenticationException {
    public TokenAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TokenAuthenticationException(String msg) {
        super(msg);
    }
}
