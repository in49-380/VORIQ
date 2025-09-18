package com.voriq.car_catalog_service.exception_handler;


import com.voriq.car_catalog_service.exception_handler.dto.ErrorResponse;
import com.voriq.car_catalog_service.exception_handler.dto.ValidationError;
import com.voriq.car_catalog_service.exception_handler.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Global REST exception handler that maps exceptions to structured {@link ErrorResponse}s.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Convert validation/parse/auth/rate-limit errors into appropriate HTTP statuses.</li>
 *   <li>Render business {@link RestException} as provided by the domain layer.</li>
 *   <li>Fallback to HTTP 500 for uncaught {@link RuntimeException}s.</li>
 * </ul>
 *
 * <h3>Mapping summary</h3>
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} → 400</li>
 *   <li>{@link HttpMessageNotReadableException} → 400</li>
 *   <li>{@link BadCredentialsException}, {@link AuthenticationException} → 401</li>
 *   <li>{@link RestException} → as provided by the exception (custom status/payload)</li>
 *   <li>Other {@link RuntimeException} → 500</li>
 * </ul>
 *
 * <p>Each response includes timestamp, status, reason phrase, messages, and request path.</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * Handles bean validation failures (e.g., {@code @Valid} on request DTOs).
     *
     * @param ex      validation exception with binding result
     * @param request current HTTP request (used to fill {@code path})
     * @return 400 Bad Request with a list of {@link ValidationError}s
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Set<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ValidationError.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toSet());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of("The error of validation of the request"))
                .validationErrors(validationErrors)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles method parameter validation failures (e.g., constraints on controller method
     * arguments validated via {@code @Validated}).
     *
     * <p>Triggered by {@link HandlerMethodValidationException} when Spring validates controller
     * method parameters. Produces a 400 Bad Request with collected {@link ValidationError}s.</p>
     *
     * @param ex      validation exception carrying parameter validation results
     * @param request current HTTP request (used to fill {@code path})
     * @return 400 Bad Request with a list of {@link ValidationError}s
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(HandlerMethodValidationException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Set<ValidationError> validationErrors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(err -> ValidationError.builder()//
                                .message(err.getDefaultMessage())
                                .build()))
                .collect(Collectors.toSet());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of("The error of validation of the request"))
                .validationErrors(validationErrors)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles type conversion mismatches for request parameters, path variables, or headers.
     *
     * <p>Triggered by {@link TypeMismatchException} (and its subclasses, e.g.
     * {@code MethodArgumentTypeMismatchException}) when a request value cannot be converted
     * to the required target type (e.g., invalid enum constant, number format issue).
     * Produces a 400 Bad Request with the exception message; {@code validationErrors} is not populated.</p>
     *
     * @param ex      the mismatch exception describing the failed conversion
     * @param request current HTTP request (used to fill {@code path})
     * @return 400 Bad Request containing an {@link ErrorResponse} with the error message only
     * @author RsLan
     * @since 1.0.0
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(TypeMismatchException ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of(ex.getMessage()))
                .validationErrors(null)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles malformed/ unreadable request body (e.g., invalid JSON).
     *
     * @param ex      parsing exception
     * @param request current request
     * @return 400 Bad Request with the parser message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of(ex.getMessage()))
                .path(request.getRequestURI())
                .validationErrors(null)
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles bad credentials (authentication failed).
     *
     * @param ex      authentication exception
     * @param request current request
     * @return 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of(ex.getMessage()))
                .path(request.getRequestURI())
                .validationErrors(null)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles generic Spring Security authentication errors.
     *
     * @param ex      authentication exception
     * @param request current request
     * @return 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(AuthenticationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of(ex.getMessage()))
                .path(request.getRequestURI())
                .validationErrors(null)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles domain-level REST exceptions that already encapsulate status and payload.
     *
     * @param ex      domain exception containing {@link ErrorResponse} and HTTP status
     * @param request current request
     * @return response built from the exception
     */
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleException(RestException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ex.getResponse();

        errorResponse.setPath(request.getRequestURI());
        log.error("REST Error: {}", errorResponse, ex);

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    /**
     * Fallback handler for all other runtime exceptions.
     *
     * @param ex      unexpected runtime exception
     * @param request current request
     * @return 500 Internal Server Error with a generic payload
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(Set.of(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        log.error("Some error: {}", errorResponse, ex);

        return new ResponseEntity<>(errorResponse, status);
    }
}
