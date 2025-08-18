package com.voriq.parser_service.service.interfaces;

import com.voriq.parser_service.domain.dto.ModelDto;
import java.util.List;

public interface ModelService {
    List<ModelDto> getAllModels ();
    ModelDto getModelById (Long id);
}
