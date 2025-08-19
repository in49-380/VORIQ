package com.voriq.security_service.exception_handler.exception;

import java.util.UUID;

import static com.voriq.security_service.utilitie.TokenUtilities.getMaskedUuid;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UUID id, Throwable cause) {
        super("User with id <" + id + "> not found.", cause);
    }

    public UserNotFoundException(UUID id) {
        this(id, (Throwable) null);
    }

    public UserNotFoundException(UUID id,  UUID key) {
        super("User with id <" + id + "> and key <" + getMaskedUuid(key) + ">not found.");

    }

    public UserNotFoundException(Throwable cause) {
        super("User not found", cause);
    }

}
