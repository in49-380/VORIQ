package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.ModelDto;
import com.voriq.parser_service.domain.entity.Model;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    List<ModelDto> toDtoList(List<Model> models);
    ModelDto toDto(Model model);
}
