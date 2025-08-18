package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.EngineDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface EngineApi {

    ResponseEntity<List<EngineDto>> getAllEngine();

    ResponseEntity<EngineDto> getEngineById(@PathVariable Long id);
}
