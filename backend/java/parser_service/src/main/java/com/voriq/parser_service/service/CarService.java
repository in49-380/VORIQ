package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.mapper.CarMapper;
import com.voriq.parser_service.repository.CarRepository;

import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService implements ReadOnlyService<CarDto, Long> {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarDto> getAll() {
        return carMapper.toDtoList(carRepository.findAll());
    }

    @Override
    public CarDto getById(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow());
    }
}
