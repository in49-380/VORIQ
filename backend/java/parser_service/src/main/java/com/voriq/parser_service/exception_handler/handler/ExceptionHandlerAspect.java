package com.voriq.parser_service.exception_handler.handler;

import com.voriq.parser_service.exception_handler.ObjectNotFoundException;
import com.voriq.parser_service.exception_handler.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlerAspect {
    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return getResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {
        ex.getBindingResult().getFieldError();
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return getResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(DataIntegrityViolationException ex,
                                                                 HttpServletRequest request) {
        return getResponse(HttpStatus.CONFLICT, "Data conflict occurred: " + ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " +
                ex.getMessage(), request);
    }

    public ResponseEntity<ErrorResponse> getResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return ResponseEntity
                .status(status)
                .headers(headers)
                .body(errorResponse);
    }
}
