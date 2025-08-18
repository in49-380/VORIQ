package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.domain.dto.ModelDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ModelApi {
    ResponseEntity<List<ModelDto>> getAllModels();

    ResponseEntity<ModelDto> getModelById(@PathVariable Long id);
}
