package com.voriq.parser_service.service.interfaces;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.dto.FuelTypeDto;

import java.util.List;

public interface FuelTypeService {
    List<FuelTypeDto> getAllFuelTypes ();
    FuelTypeDto getFuelTypeById (Long id);
}
