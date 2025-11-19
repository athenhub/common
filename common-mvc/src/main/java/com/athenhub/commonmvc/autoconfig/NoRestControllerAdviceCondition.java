package com.athenhub.commonmvc.autoconfig;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Condition 클래스.
 *
 * <p>이 조건은 스프링 컨텍스트 내에 {@link RestControllerAdvice} 애노테이션이 붙은 빈이 존재하지 않을 때만 true를 반환한다.
 *
 * <p>즉, 사용자가 직접 전역 예외 처리기(ControllerAdvice)를 구현했을 경우 라이브러리가 제공하는 기본 MvcExceptionHandler 자동
 * 구성(AutoConfiguration)은 비활성화된다.
 *
 * <p>Spring Boot의 AutoConfiguration 철학인 “사용자가 명시적으로 제공한 빈이 있다면 기본 설정을 끈다” 를 따르기 위한 구성이다.
 *
 * @author 김지원
 * @since 0.2.0
 */
public class NoRestControllerAdviceCondition implements Condition {

  /**
   * AutoConfiguration 매칭 여부를 판단하는 메서드.
   *
   * @param context 현재 스프링 컨텍스트에 접근할 수 있는 Context
   * @param metadata 애노테이션 메타 정보 (사용되지 않음)
   * @return true — RestControllerAdvice 빈이 하나도 없는 경우 false — 하나라도 존재하면 AutoConfiguration 비활성화
   */
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    if (context.getBeanFactory() == null) {
      return false;
    }

    String[] beanNames =
        context.getBeanFactory().getBeanNamesForAnnotation(RestControllerAdvice.class);

    return beanNames.length == 0;
  }
}
