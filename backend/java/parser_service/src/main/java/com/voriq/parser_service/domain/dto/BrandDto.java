package com.voriq.parser_service.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
//@Schema(name = "X dto", description = "Return new X")
public class BrandDto {

    //@Schema(description = "description", example = "example")
    private Long id;
    private String name;
}
