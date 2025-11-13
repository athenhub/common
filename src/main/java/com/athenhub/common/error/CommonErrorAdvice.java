package com.athenhub.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonErrorAdvice {
    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e) {
        log.error("ApplicationException", e);

        return ResponseEntity
                .status(e.getStatusCode())
                .body(
                        new ErrorResponse(e.getCode(), e.getMessage())
                );
    }
}
