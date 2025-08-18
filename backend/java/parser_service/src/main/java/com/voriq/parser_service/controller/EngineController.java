package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.EngineApi;
import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.service.interfaces.BrandService;
import com.voriq.parser_service.service.interfaces.EngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/engines")
@RequiredArgsConstructor
public class EngineController implements EngineApi {

    private final EngineService engineService;

    @Override
    @GetMapping
    public ResponseEntity<List<EngineDto>> getAllEngine() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(engineService.getAllEngines());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EngineDto> getEngineById(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(engineService.getEngineById(id));
    }
}
