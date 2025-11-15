package com.athenhub.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

    private final String code;
    private final String message;
    private final T details;

    private ErrorResponse(String code, String message, T details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static <T> ErrorResponse<T> of(String code, String message) {
        return new ErrorResponse<>(code, message, null);
    }

    public static <T> ErrorResponse<T> of(String code, String message, T details) {
        return new ErrorResponse<>(code, message, details);
    }

}
