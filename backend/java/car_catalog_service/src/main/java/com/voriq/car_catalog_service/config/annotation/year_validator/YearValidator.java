package com.voriq.car_catalog_service.config.annotation.year_validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;
import java.time.ZoneId;

public class YearValidator
        implements ConstraintValidator<CurrentYearOrEarlier, Integer> {

    private ZoneId zone;

    @Override
    public void initialize(CurrentYearOrEarlier ann) {
        this.zone = ZoneId.of(ann.zoneId());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        int current = Year.now(zone).getValue();
        return value <= current;
    }
}
