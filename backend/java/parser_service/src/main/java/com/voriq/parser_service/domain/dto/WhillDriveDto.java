package com.voriq.parser_service.domain.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WhillDriveDto {

    private Long id;
    private String name;
}
