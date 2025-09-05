package com.voriq.parser_service.domain.dto;

import com.voriq.parser_service.domain.entity.Market;
import com.voriq.parser_service.domain.entity.Transmission;
import com.voriq.parser_service.domain.entity.WhillDrive;
import com.voriq.parser_service.domain.entity.Year;
import jakarta.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CarDto {

    private Long id;
    private ModelDto model;
    private EngineDto engine;
    private YearDto year;
    private MarketDto market;
    private TransmissionDto transmission;
    private WhillDriveDto whilldrive;
}
