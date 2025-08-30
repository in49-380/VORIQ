package com.voriq.car_catalog_service.domain.dto.abstracts;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class ItemResponseDto<T> {

    private List<T> items;
}
