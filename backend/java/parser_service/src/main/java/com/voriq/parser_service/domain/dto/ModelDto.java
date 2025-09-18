package com.voriq.parser_service.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelDto {

    private Long id;
    private String name;
    private BrandDto brand;
}
