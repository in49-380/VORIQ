package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
}
