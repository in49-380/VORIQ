package com.voriq.car_catalog_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(name = "Dto with car id")
public class CarIdDto {

   @Schema(name = "Car ID",example = "12")
   private Long carId;
}
