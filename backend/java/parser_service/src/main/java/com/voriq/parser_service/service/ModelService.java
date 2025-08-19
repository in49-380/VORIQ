package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.domain.dto.ModelDto;
import com.voriq.parser_service.mapper.ModelMapper;
import com.voriq.parser_service.repository.ModelRepository;
import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService implements ReadOnlyService<ModelDto, Long> {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ModelDto> getAll() {
        return modelMapper.toDtoList(modelRepository.findAll());
    }

    @Override
    public ModelDto getById(Long id) {
        return modelMapper.toDto(modelRepository.findById(id).orElseThrow());
    }
}
