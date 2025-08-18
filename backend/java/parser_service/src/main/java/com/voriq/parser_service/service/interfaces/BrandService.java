package com.voriq.parser_service.service.interfaces;

import com.voriq.parser_service.domain.dto.BrandDto;
import java.util.List;

public interface BrandService {
    List<BrandDto> getAllBrands ();
    BrandDto getBrandById (Long id);
}
