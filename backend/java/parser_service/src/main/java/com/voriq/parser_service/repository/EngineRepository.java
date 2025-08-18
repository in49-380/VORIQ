package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineRepository extends JpaRepository<Engine, Long> {
}
