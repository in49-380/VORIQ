package com.voriq.car_catalog_service.domain.dto;

import com.voriq.car_catalog_service.domain.dto.abstracts.PageResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor

@Schema(description = "Cars paginated response ")
public class PageCarResponseDto extends PageResponseDto<CarResponseDto> {
    public PageCarResponseDto(Page<CarResponseDto> page) {
        super(page);
    }
}

