package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.BrandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/brands")
public interface BrandApi {

    @GetMapping
    ResponseEntity<List<BrandDto>> getAllBrands();

    @GetMapping("/{id}")
    ResponseEntity<BrandDto> getBrandById(@PathVariable("id") Long id);
}
