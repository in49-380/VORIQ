package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {
    @Mapping(source = "fuelType", target = "engine.fuelType")
    List<CarDto> toDtoList(List<Car> cars);

    @Mapping(source = "engine.fuelType", target = "fuelType")
    CarDto toDto(Car car);
}
