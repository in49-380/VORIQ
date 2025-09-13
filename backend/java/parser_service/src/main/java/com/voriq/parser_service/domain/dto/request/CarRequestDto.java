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

    private String brand;
    private String name;
    private String year;

    @JsonProperty("Engine type")
    @NotNull(message = "Engine type cannot be null")
    private String engineType;

    @JsonProperty("Fuel type")
    @NotNull(message = "Fuel type cannot be null")
    private String fuelType;

    @JsonProperty("Transmission type")
    @NotNull(message = "Transmission type cannot be null")
    private String transmissionType;

    @JsonProperty("Number of gears")
    private Integer gears;

    @JsonProperty("Drive")
    private String drive;
}
