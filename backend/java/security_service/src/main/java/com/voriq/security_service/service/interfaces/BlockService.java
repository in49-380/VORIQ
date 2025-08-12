package com.voriq.security_service.service.interfaces;

import java.util.UUID;

public interface BlockService {

    void block(UUID userId);

    boolean isBlocked(UUID userId);
}
