package com.voriq.security_service.exception_handler.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(long retryAfterSeconds) {
        super("Too many requests. Try again in " + retryAfterSeconds + "s.");
    }
}
