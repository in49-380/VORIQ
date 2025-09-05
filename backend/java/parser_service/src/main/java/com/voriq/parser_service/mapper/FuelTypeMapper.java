package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.domain.entity.Engine;
import com.voriq.parser_service.domain.entity.FuelType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuelTypeMapper {
    List<FuelTypeDto> toDtoList(List<FuelType> fuelTypes);
    FuelTypeDto toDto(FuelType fuelType);
}
