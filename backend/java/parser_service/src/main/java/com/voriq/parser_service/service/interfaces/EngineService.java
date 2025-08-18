package com.voriq.parser_service.service.interfaces;
import com.voriq.parser_service.domain.dto.EngineDto;
import java.util.List;

public interface EngineService {
    List<EngineDto> getAllEngines ();
    EngineDto getEngineById (Long id);
}
