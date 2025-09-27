package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransmissionRepository extends JpaRepository<Transmission, Long> {
    Optional<Transmission> findByMarketingNameIgnoreCaseAndGearsAndManual(
            String marketingName,
            Integer gears,
            Boolean manual
    );
}
