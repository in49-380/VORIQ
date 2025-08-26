package com.voriq.car_catalog_service.domain.dto;

import com.voriq.car_catalog_service.domain.dto.abstracts.ItemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Models response")
public class ModelResponseDto extends ItemResponseDto<String> {
}
