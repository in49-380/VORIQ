package com.voriq.car_catalog_service.domain.dto;

import com.voriq.car_catalog_service.domain.dto.abstracts.ItemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Engine types response")
public class EngineResponseDto extends ItemResponseDto<String> {
}
