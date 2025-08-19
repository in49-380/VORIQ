package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.mapper.EngineMapper;
import com.voriq.parser_service.repository.EngineRepository;
import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineService implements ReadOnlyService<EngineDto, Long> {

    private final EngineRepository engineRepository;
    private final EngineMapper engineMapper;

    @Override
    public List<EngineDto> getAll() {
        return engineMapper.toDtoList(engineRepository.findAll());
    }

    @Override
    public EngineDto getById(Long id) {
        return engineMapper.toDto(engineRepository.findById(id).orElseThrow());
    }
}
