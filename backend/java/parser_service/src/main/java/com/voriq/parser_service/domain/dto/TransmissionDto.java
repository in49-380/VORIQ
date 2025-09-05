package com.voriq.parser_service.domain.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TransmissionDto {

    private Long id;
    private Integer gears;
    private String supplier;
    private String family_code;
    private String marketingName;
    private Boolean manual;
}
