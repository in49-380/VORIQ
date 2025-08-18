package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.FuelTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface FuelTypeApi {

    ResponseEntity<List<FuelTypeDto>> getAllFuelTypes();

    ResponseEntity<FuelTypeDto> getFuelTypeById(@PathVariable Long id);
}
