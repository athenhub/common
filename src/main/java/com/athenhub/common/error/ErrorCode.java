package com.athenhub.common.error;

import org.springframework.http.HttpStatusCode;

public interface ErrorCode {

    HttpStatusCode getStatus();

    String getCode();
}
