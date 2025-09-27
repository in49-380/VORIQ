package com.voriq.parser_service.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequestDto {

    @NotNull(message = "Brand cannot be null")
    private String brand;

    @NotNull(message = "Model cannot be null")
    private String model; // name of model

    private Integer year;

    @JsonProperty("marketing_name")
    private String marketingName;

    @JsonProperty("displacement")
    private Double displacementCC;

    @JsonProperty("engine_type")
    @NotNull(message = "Engine type cannot be null")
    private String engineType;

    @JsonProperty("fuel_type")
    private String fuelType;

    @JsonProperty("transmission_type")
    private Boolean manual; // manual of transmission

    @JsonProperty("number_gears")
    private Integer gears;

    @JsonProperty("drive")
    private String drive; // name of whilldrive
}
