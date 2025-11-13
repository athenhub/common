package com.athenhub.common.error;

import org.springframework.stereotype.Component;

@Component
public class ExceptionResolver {

    public String resolve() {
        return "resolved";
    }
}
