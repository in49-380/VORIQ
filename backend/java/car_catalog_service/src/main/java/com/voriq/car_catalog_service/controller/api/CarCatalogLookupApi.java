package com.voriq.car_catalog_service.controller.api;

import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.exception_handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Car catalog lookup", description = "Endpoints of the car catalog for sequential search of cars by parameters.")
@RequestMapping("/v1/catalog")
public interface CarCatalogLookupApi {

    @Operation(summary = "Get all brands",
            description = "Get all brands from catalog. For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "User does not authorized.",
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
    @GetMapping("/brands")
    ResponseEntity<List<IdValueResponseDto>> getALlBrands();
    //===============================================

    @Operation(summary = "Get models of car by brand",
            description = "Get models of car by brand from catalog. For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
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
    @GetMapping("/brands/{brandId}/models")
    ResponseEntity<List<IdValueResponseDto>> getModelsByBrand(
            @PathVariable
            @Parameter(description = "Id of brand in catalog", example = "32")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long brandId
    );
    //===============================================

    @Operation(summary = "Get years of car by brand and model",
            description = "Get years of car by brand ID and model ID from catalog. For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
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
    @GetMapping("/brands/{brandId}/models/{modelId}/years")
    ResponseEntity<List<IdValueResponseDto>> getYears(
            @PathVariable
            @Parameter(description = "Id of brand in catalog", example = "32")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long brandId,
            @PathVariable
            @Parameter(description = "Id of model in catalog", example = "43")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long modelId
    );
    //===============================================

    @Operation(summary = "Get engines of car by brand, model and year",
            description = "Get engines of car by brand ID, model ID and year ID from catalog. " +
                    "For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
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
    @GetMapping("/brands/{brandId}/models/{modelId}/years/{yearId}/engines")
    ResponseEntity<List<IdValueResponseDto>> getEngines(
            @PathVariable
            @Parameter(description = "Id of brand in catalog", example = "32")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long brandId,
            @PathVariable
            @Parameter(description = "Id of model in catalog", example = "43")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long modelId,
            @PathVariable
            @Parameter(description = "Id of year in catalog", example = "5")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long yearId
    );
    //===============================================

    @Operation(summary = "Get transmissions of car by brand, model, year and engine",
            description = "Get transmissions of car by brand ID, model ID, year ID and engine ID from catalog. " +
                    "For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
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
    @GetMapping("/brands/{brandId}/models/{modelId}/years/{yearId}/engines/{engineId}/transmissions")
    ResponseEntity<List<IdValueResponseDto>> getTransmissions(
            @PathVariable
            @Parameter(description = "Id of brand in catalog", example = "32")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long brandId,
            @PathVariable
            @Parameter(description = "Id of model in catalog", example = "43")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long modelId,
            @PathVariable
            @Parameter(description = "Id of year in catalog", example = "5")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long yearId,
            @PathVariable
            @Parameter(description = "Id of engine in catalog", example = "6")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long engineId
    );
    //===============================================

    @Operation(summary = "Get wheel drive of car by brand, model, year, engine and transmission",
            description = "Get wheel drive of car by brand ID, model ID, year ID," +
                    " engine ID and transmission ID from catalog. " +
                    "For authorized user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = IdValueResponseDto.class))
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
    @GetMapping("/brands/{brandId}/models/{modelId}/years/{yearId}/" +
            "engines/{engineId}/transmissions/{transmissionsId}/wheel_drive")
    ResponseEntity<List<IdValueResponseDto>> getWheelDrives(
            @PathVariable
            @Parameter(description = "Id of brand in catalog", example = "32")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long brandId,
            @PathVariable
            @Parameter(description = "Id of model in catalog", example = "43")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long modelId,
            @PathVariable
            @Parameter(description = "Id of year in catalog", example = "5")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long yearId,
            @PathVariable
            @Parameter(description = "Id of engine in catalog", example = "6")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long engineId,
            @PathVariable
            @Parameter(description = "Id of transmission in catalog", example = "2")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long transmissionsId
    );
    //===============================================

    @Operation(summary = "Return ID of the desired car",
            description = "Return from catalog ID of the desired car from catalog by parameters ID." +
                    "For authorized user",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CarResolveRequest.class))))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful getting",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CarIdDto.class)
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
                    description = "Car not found",
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
    @PostMapping("/cars/resolve")
    ResponseEntity<CarIdDto> resolve(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            CarResolveRequest req);

}
