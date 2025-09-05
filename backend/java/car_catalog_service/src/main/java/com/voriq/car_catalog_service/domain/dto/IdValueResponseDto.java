package com.voriq.car_catalog_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(name = "Dto for transferring the ID and the value of the auto parameter")
public class IdValueResponseDto {

    @Schema(name = "Id of parameter", example = "3")
    private Long id;

    @Schema(name = "Value of parameter", example = "Some string value")
    private String value;
}
