package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.BrandApi;
import com.voriq.parser_service.controller.api.ModelApi;
import com.voriq.parser_service.domain.dto.ModelDto;
import com.voriq.parser_service.service.interfaces.BrandService;
import com.voriq.parser_service.service.interfaces.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
public class ModelController implements ModelApi {

    private final ModelService modelService;

    @Override
    @GetMapping
    public ResponseEntity<List<ModelDto>> getAllModels() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelService.getAllModels());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ModelDto> getModelById(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelService.getModelById(id));
    }
}
