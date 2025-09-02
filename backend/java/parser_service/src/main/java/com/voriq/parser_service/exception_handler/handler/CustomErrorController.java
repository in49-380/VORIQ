package com.voriq.parser_service.exception_handler.handler;

import com.voriq.parser_service.exception_handler.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        HttpStatus httpStatus = status != null
                ? HttpStatus.valueOf((Integer) status)
                : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message("The requested resource was not found or the method is not supported.")
                .path(request.getAttribute("jakarta.servlet.error.request_uri").toString())
                .build();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
