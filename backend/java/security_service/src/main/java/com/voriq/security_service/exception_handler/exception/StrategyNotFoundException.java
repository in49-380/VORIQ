package com.voriq.security_service.exception_handler.exception;

public class StrategyNotFoundException extends NotFoundException{
    public StrategyNotFoundException(String message) {
        super(message);
    }

    public StrategyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
