package com.voriq.parser_service.service.interfaces;

import java.util.List;

public interface ReadOnlyService<D, ID> {
    List<D> getAll();
    D getById(ID id);
}
