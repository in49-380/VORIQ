package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineRepository extends JpaRepository<Engine, Long> {
    Optional<Engine> findByTypeIgnoreCaseAndDisplacementCC(String name, Double displacementCC);
}