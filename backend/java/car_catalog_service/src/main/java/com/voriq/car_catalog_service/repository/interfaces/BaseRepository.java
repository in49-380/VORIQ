package com.voriq.car_catalog_service.repository.interfaces;

import java.util.List;

public interface BaseRepository<T> {

     List<T> findAll(Class<T> type);
}
