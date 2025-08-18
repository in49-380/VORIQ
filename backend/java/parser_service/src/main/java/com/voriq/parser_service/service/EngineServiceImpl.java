package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.mapper.BrandMapper;
import com.voriq.parser_service.mapper.EngineMapper;
import com.voriq.parser_service.repository.BrandRepository;
import com.voriq.parser_service.repository.EngineRepository;
import com.voriq.parser_service.service.interfaces.EngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineServiceImpl implements EngineService {

    private final EngineRepository engineRepository;
    private final EngineMapper engineMapper;

    @Override
    public List<EngineDto> getAllEngines() {
        return engineMapper.toDtoList(engineRepository.findAll());
    }

    @Override
    public EngineDto getEngineById(Long id) {
        return engineMapper.toDto(engineRepository.findById(id).orElseThrow());
    }
}
