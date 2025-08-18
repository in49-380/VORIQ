package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.mapper.EngineMapper;
import com.voriq.parser_service.mapper.FuelTypeMapper;
import com.voriq.parser_service.repository.EngineRepository;
import com.voriq.parser_service.repository.FuelTypeRepository;
import com.voriq.parser_service.service.interfaces.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeServiceImpl implements FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;
    private final FuelTypeMapper fuelTypeMapper;

    @Override
    public List<FuelTypeDto> getAllFuelTypes() {
        return fuelTypeMapper.toDtoList(fuelTypeRepository.findAll());
    }

    @Override
    public FuelTypeDto getFuelTypeById(Long id) {
        return fuelTypeMapper.toDto(fuelTypeRepository.findById(id).orElseThrow());
    }

}
