package com.voriq.car_catalog_service.exception_handler.exception;



public class PaginationParameterIsWrongException extends BadRequestException {

    public PaginationParameterIsWrongException(int page, int size, String sortBy) {

        super(String.format("Parameters of pagination is wrong :" +
                " page <%d>, size <%d>, sortBy <%s>", page, size, sortBy));
    }
}

