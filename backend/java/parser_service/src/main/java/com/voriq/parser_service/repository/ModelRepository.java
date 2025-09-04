package com.voriq.parser_service.repository;

import com.voriq.parser_service.domain.entity.Brand;
import com.voriq.parser_service.domain.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByNameIgnoreCaseAndBrand(String name, Brand brand);
}
