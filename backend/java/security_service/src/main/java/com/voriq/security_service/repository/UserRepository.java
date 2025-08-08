package com.voriq.security_service.repository;

import com.voriq.security_service.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(UUID userId);

    @Query("SELECT u.key FROM User u WHERE u.userId = :userId")
    UUID findKeyOnlyByUserId(@Param("userId") UUID userId);
}
