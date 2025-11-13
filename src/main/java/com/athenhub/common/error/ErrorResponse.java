package com.athenhub.common.error;

public record ErrorResponse(
        String code,
        String message
) {
}
