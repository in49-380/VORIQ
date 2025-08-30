package com.voriq.car_catalog_service.controller.api;

import com.voriq.car_catalog_service.config.annotation.year_validator.CurrentYearOrEarlier;
import com.voriq.car_catalog_service.domain.dto.CarResponseDto;
import com.voriq.car_catalog_service.domain.dto.PageCarResponseDto;
import com.voriq.car_catalog_service.exception_handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Cars", description = "Cars catalog endpoints")
@RequestMapping("/v1/cars")
public interface CarApi {

    String PAGE_VALUE = "0";
    String SIZE_VALUE = "10";
    String SORT_BY = "brand";

    @Operation(summary = "Get all cars.",
            description = "Get all cars by parameters from catalog. If parameters are not specified, " +
                    "all cars are returned. Return with pagination. For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageCarResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "User does not authorized.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Content not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Temporary service error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "503",
                    description = "The server is currently overloaded or under maintenance. Please try again later.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))}
    )
    @GetMapping
    ResponseEntity<PageCarResponseDto> searchCars(
            @RequestParam(required = false)
            @Parameter(description = "Name of brand.", example = "Audi")
            @Size(max = 30, message = "Max size of a brand name cannot be more than 30 characters.")
            String brand,

            @RequestParam(required = false)
            @Parameter(description = "Name of model.", example = "Q5")
            @Size(max = 30, message = "Max size of a model name cannot be more than 30 characters.")
            String model,

            @RequestParam(required = false)
            @Parameter(description = "Type of fuel.", example = "diesel")
            @Size(max = 30, message = "Max size of a type of fuel cannot be more than 30 characters.")
            String fuelType,

            @RequestParam(required = false)
            @Parameter(description = "Type of engine.", example = "ICE")
            @Size(max = 30, message = "Max size of a type of engine cannot be more than 30 characters.")
            String engineType,

            @RequestParam(required = false)
            @Parameter(description = "Search since year.", example = "2015")
            @Min(value = 2015, message = "Year less than minimum allowed.")
            @CurrentYearOrEarlier
            Integer yearFrom,

            @RequestParam(required = false)
            @Parameter(description = "Search by year.", example = "2020")
            @Min(value = 2015, message = "Year less than minimum allowed.")
            @CurrentYearOrEarlier
            Integer yearTo,

            @RequestParam(defaultValue = PAGE_VALUE)
            @Parameter(description = "Requested page number.", example = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = SIZE_VALUE)
            @Parameter(description = "Number of entities per page.", example = "10")
            @Min(1)
            int size,

            @RequestParam(defaultValue = SORT_BY)
            @Parameter(description = "Sorting field.", examples = {
                    @ExampleObject(name = "Sort by brand (default)", value = "brand"),
                    @ExampleObject(name = "Sort by year", value = "year"),
                    @ExampleObject(name = "Sort by engine type", value = "engineType"),
                    @ExampleObject(name = "Sort by fuel type", value = "fuelType")
            })
            String sortBy,

            @RequestParam(defaultValue = "true")
            @Parameter(description = "Sorting direction.", examples = {
                    @ExampleObject(name = "Sort direction is ascending (default)", value = "true"),
                    @ExampleObject(name = "Sort direction is descending", value = "false")
            })
            Boolean isAsc
    );

    @Operation(summary = "Get car by id",
            description = "Get car by id from catalog. For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CarResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "User does not authorized.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Content not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Temporary service error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "503",
                    description = "The server is currently overloaded or under maintenance. Please try again later.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))}
    )
    @GetMapping("/by-id/{id}")
    ResponseEntity<CarResponseDto> getById(
            @RequestParam
            @Parameter(description = "Car id in catalog.", example = "3")
            @NotNull
            @Min(value = 1, message = "ID can't be less 1.")
            Long id);
}
