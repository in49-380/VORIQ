package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.entity.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EngineMapper {

    @Mapping(source = "fuelType", target = "fuelType")
    List<EngineDto> toDtoList(List<Engine> engines);

    @Mapping(source = "fuelType", target = "fuelType")
    EngineDto toDto(Engine engine);
}
