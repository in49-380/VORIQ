package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.ModelDto;
import com.voriq.parser_service.mapper.BrandMapper;
import com.voriq.parser_service.mapper.ModelMapper;
import com.voriq.parser_service.repository.BrandRepository;
import com.voriq.parser_service.repository.ModelRepository;
import com.voriq.parser_service.service.interfaces.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ModelDto> getAllModels() {
        return modelMapper.toDtoList(modelRepository.findAll());
    }

    @Override
    public ModelDto getModelById(Long id) {
        return modelMapper.toDto(modelRepository.findById(id).orElseThrow());
    }
}
