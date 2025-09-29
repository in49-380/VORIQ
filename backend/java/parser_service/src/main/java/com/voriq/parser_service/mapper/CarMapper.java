package com.voriq.parser_service.mapper;

import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "model", target = "model")
    @Mapping(source = "engine", target = "engine")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "market", target = "market")
    @Mapping(source = "transmission", target = "transmission")
    @Mapping(source = "whilldrive", target = "whilldrive")
    List<CarDto> toDtoList(List<Car> cars);

    @Mapping(source = "model", target = "model")
    @Mapping(source = "engine", target = "engine")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "market", target = "market")
    @Mapping(source = "transmission", target = "transmission")
    @Mapping(source = "whilldrive", target = "whilldrive")
    CarDto toDto(Car car);
}