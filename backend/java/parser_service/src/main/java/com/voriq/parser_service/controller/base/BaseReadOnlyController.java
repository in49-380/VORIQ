package com.voriq.parser_service.controller.base;

import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public abstract class BaseReadOnlyController<D, ID> {

    protected final ReadOnlyService<D, ID> service;

    protected BaseReadOnlyController(ReadOnlyService<D, ID> service) {
        this.service = service;
    }

    @Operation(summary = "Get all")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class) // заменить на кастомный клас ошибки
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable ID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
