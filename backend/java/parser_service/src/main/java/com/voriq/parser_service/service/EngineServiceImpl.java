package com.voriq.parser_service.service;

import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.exception_handler.ObjectNotFoundException;
import com.voriq.parser_service.exception_handler.errormessage.ErrorMessage;
import com.voriq.parser_service.mapper.EngineMapper;
import com.voriq.parser_service.repository.EngineRepository;
import com.voriq.parser_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineServiceImpl implements ReadOnlyService<EngineDto, Long> {

    private final EngineRepository engineRepository;
    private final EngineMapper engineMapper;

    @Override
    public List<EngineDto> getAll() {
        return engineMapper.toDtoList(engineRepository.findAll());
    }

    @Override
    public EngineDto getById(Long id) {
        return engineMapper.toDto(engineRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException(ErrorMessage.OBJECT_NOT_FOUND)));
    }
}
