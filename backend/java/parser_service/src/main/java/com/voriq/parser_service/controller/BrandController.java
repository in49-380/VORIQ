package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.BrandApi;
import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.service.interfaces.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController implements BrandApi {

    private final BrandService brandService;

    @Override
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(brandService.getAllBrands());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(brandService.getBrandById(id));
    }
}
