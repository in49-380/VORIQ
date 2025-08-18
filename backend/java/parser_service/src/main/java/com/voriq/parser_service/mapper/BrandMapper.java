package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.domain.entity.Brand;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    List<BrandDto> toDtoList(List<Brand> brands);
    BrandDto toDto(Brand brand);
}
