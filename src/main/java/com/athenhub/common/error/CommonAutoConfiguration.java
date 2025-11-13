package com.athenhub.common.error;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration // 자동 구성 설정 클래스임을 명시
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET) // 이 자동 구성 전체가 웹 환경일 때만 활성화되도록 조건을 추가 (필수)
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExceptionResolver exceptionResolver() {
        return new ExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommonErrorAdvice commonErrorAdvice() {
        return new CommonErrorAdvice();
    }

}
