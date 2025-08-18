package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.BrandApi;
import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.service.interfaces.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BrandController implements BrandApi {

    private final BrandService brandService;

    @Override
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        System.out.println("List<BrandDto> send!!!");
        return ResponseEntity.status(HttpStatus.OK)
                .body(brandService.getAllBrands());
    }

    @Override
    public ResponseEntity<BrandDto> getBrandById(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(brandService.getBrandById(id));
//        return brandService.getBrandById(id);
    }
}
