package com.athenhub.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpStatusCodeException;

@Getter
public class ApplicationException extends HttpStatusCodeException {

    private String code;
    private String message;

    public ApplicationException(HttpStatusCode statusCode, String code, String message) {
        super(statusCode);
        this.code = code;
        this.message = message;
    }
}
