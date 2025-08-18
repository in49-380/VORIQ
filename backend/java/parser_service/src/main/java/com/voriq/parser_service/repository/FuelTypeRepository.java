package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Engine;
import com.voriq.parser_service.domain.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeRepository  extends JpaRepository<FuelType, Long> {
}
