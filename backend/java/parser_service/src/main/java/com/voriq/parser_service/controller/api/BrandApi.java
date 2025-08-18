package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.BrandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

public interface BrandApi {
    ResponseEntity<List<BrandDto>> getAllBrands();
    ResponseEntity<BrandDto> getBrandById(@PathVariable Long id);
}
