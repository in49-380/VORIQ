package com.voriq.car_catalog_service.controller.base;

import com.voriq.car_catalog_service.controller.api.BaseApi;
import com.voriq.car_catalog_service.service.interfaces.ReadOnlyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Profile("dev")
public abstract class BaseReadOnlyController<D> implements BaseApi<D> {

    ReadOnlyService<D> service;

    @Override
    public ResponseEntity<D> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

}