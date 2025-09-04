package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.WhillDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhillDriveRepository extends JpaRepository<WhillDrive, Long> {
    Optional<WhillDrive> findByNameIgnoreCase(String name);
}
