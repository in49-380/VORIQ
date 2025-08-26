package com.voriq.car_catalog_service.config.annotation.year_validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearValidator.class)
public @interface CurrentYearOrEarlier {
    String message() default "Year must not be in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String zoneId() default "Europe/Berlin";
}

