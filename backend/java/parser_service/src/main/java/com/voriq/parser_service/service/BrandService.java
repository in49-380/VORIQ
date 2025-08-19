package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.mapper.BrandMapper;
import com.voriq.parser_service.repository.BrandRepository;
import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService implements ReadOnlyService<BrandDto, Long> {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<BrandDto> getAll() {
        return brandMapper.toDtoList(brandRepository.findAll());
    }

    @Override
    public BrandDto getById(Long id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow());
    }

}
