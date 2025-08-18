package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.mapper.BrandMapper;
import com.voriq.parser_service.repository.BrandRepository;
import com.voriq.parser_service.service.interfaces.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<BrandDto> getAllBrands() {
        return brandMapper.toDtoList(brandRepository.findAll());
    }

    @Override
    public BrandDto getBrandById(Long id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow());
    }
}
