package com.voriq.car_catalog_service.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IdLabelDto<T> {

    private Long id;

    private T label;
}
