package com.athenhub.common.error;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration // 자동 구성 설정 클래스임을 명시
public class CommonAutoConfiguration {

    @ConditionalOnMissingBean(ExceptionResolver.class) // 사용자가 직접 정의하지 않았을 때만 등록
    @Bean
    public ExceptionResolver exceptionResolver() {
        return new ExceptionResolver();
    }
}
