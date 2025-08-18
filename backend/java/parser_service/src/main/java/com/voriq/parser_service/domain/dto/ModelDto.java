package com.voriq.parser_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ModelDto {
    private Long id;
    private String name;
    private BrandDto brand;
}
