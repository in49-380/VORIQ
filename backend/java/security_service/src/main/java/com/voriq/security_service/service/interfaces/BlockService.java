package com.voriq.security_service.service.interfaces;

import java.util.UUID;

public interface BlockService {

    boolean block(UUID userId);

    boolean isBlocked(UUID userId);

    boolean removeBlock(UUID userid);
}
