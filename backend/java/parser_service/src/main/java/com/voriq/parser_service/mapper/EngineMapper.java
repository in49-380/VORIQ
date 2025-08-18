package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.entity.Engine;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    List<EngineDto> toDtoList(List<Engine> engines);
    EngineDto toDto(Engine engine);
}
