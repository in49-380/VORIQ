package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.exception_handler.ObjectNotFoundException;
import com.voriq.parser_service.exception_handler.errormessage.ErrorMessage;
import com.voriq.parser_service.mapper.FuelTypeMapper;
import com.voriq.parser_service.repository.FuelTypeRepository;
import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeServiceImpl implements ReadOnlyService<FuelTypeDto, Long> {

    private final FuelTypeRepository fuelTypeRepository;
    private final FuelTypeMapper fuelTypeMapper;

    @Override
    public List<FuelTypeDto> getAll() {
        return fuelTypeMapper.toDtoList(fuelTypeRepository.findAll());
    }

    @Override
    public FuelTypeDto getById(Long id) {
        return fuelTypeMapper.toDto(fuelTypeRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException(ErrorMessage.OBJECT_NOT_FOUND)));
    }

}
