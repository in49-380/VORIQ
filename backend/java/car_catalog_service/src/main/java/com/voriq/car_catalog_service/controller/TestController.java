package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.exception_handler.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
            Long delay
    ) {
        if(delay==null) {
            throw new BadRequestException("Delay can not be null");
        }
        if (delay < 1_000L || delay > 60_000L) {
            throw new BadRequestException("Delay should be more than 1 000ms and be less than 60 000ms");
        }
        try {

            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ResponseEntity.noContent().build();
    }
}

