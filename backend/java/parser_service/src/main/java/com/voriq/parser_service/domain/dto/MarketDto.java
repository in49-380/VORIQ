package com.voriq.parser_service.domain.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MarketDto {

    private Long id;
    private String code;
    private String name;
}
