package com.voriq.car_catalog_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.voriq.car_catalog_service.config.ApiPaths.DELAY;
import static com.voriq.car_catalog_service.config.ApiPaths.TEST_BASE_URL;

@RestController
@RequestMapping(TEST_BASE_URL)
@Profile("dev")
public class TestController {


    @Operation(summary = "Get 204 after delay",
            description = "Get 204 after delay. Delay in ms. MIN -1 000(1s). MAX-60 000(60s) ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful getting"

            )}
    )
    @GetMapping(value = DELAY)
    public ResponseEntity<Void> delay(
            @RequestParam(defaultValue = "5000")
            @NotNull
            @Min(value = 1_000, message = "Delay should be more than 1 000ms")
            @Max(value = 60_000, message = "Delay should be less than 60 000ms")
            long delay
    ) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ResponseEntity.noContent().build();
    }
}

