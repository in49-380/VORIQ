package com.voriq.parser_service.service.interfaces;

import com.voriq.parser_service.domain.dto.request.CarRequestDto;

import java.util.List;

public interface CarWriteService {
    void saveCars(List<CarRequestDto> cars);
}
