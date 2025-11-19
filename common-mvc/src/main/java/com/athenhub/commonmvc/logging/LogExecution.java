package com.athenhub.commonmvc.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 실행 시점의 로깅을 처리하기 위한 커스텀 애노테이션.
 *
 * <pre>
 * - AOP를 통해 해당 애노테이션이 적용된 메서드의 시작과 종료 시점에 로그를 남김
 * - 메서드 실행 시간 측정 등 추가적인 로깅 부가 기능 적용 가능
 * </pre>
 *
 * @author 김형섭
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {}
