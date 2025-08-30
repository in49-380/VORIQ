package com.voriq.car_catalog_service.exception_handler.exception;

import java.util.UUID;


public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UUID id, Throwable cause) {
        super("User with id <" + id + "> not found.", cause);
    }

    public UserNotFoundException(UUID id) {
        this(id, (Throwable) null);
    }

    public UserNotFoundException(Throwable cause) {
        super("User not found", cause);
    }

}
