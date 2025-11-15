package com.athenhub.common.error;

import lombok.Getter;

@Getter
public class FieldError {

    private final String field;
    private final Object value;
    private final String reason;


    private FieldError(String field, Object value, String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    public static FieldError of(String field, Object value, String reason) {
        return new FieldError(field, value, reason);
    }

    public static FieldError global(String reason) {
        return new FieldError("global", null, reason);
    }
}
