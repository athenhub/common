package com.athenhub.commonmvc.autoconfig;

import com.athenhub.commoncore.message.MessageResolver;
import com.athenhub.commonmvc.message.MessageSourceResolver;
import com.athenhub.commonmvc.handler.MvcExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * MVC 환경에서 사용할 기본 {@link MvcExceptionHandler} 와 메시지 처리기({@link MessageResolver})를 자동으로 등록하는
 * AutoConfiguration.
 *
 * <p>이 구성은 다음 조건을 모두 충족할 때만 활성화된다:
 *
 * <ul>
 *   <li>현재 애플리케이션이 Servlet 기반 Web MVC 환경일 것
 *   <li><code>athenhub.exception.mvc.enabled=true</code> 이거나 설정이 없을 것
 *   <li>사용자가 직접 <code>@RestControllerAdvice</code> 기반 글로벌 예외 처리기를 등록하지 않았을 것
 *   <li>{@link MvcExceptionHandler} 또는 {@link MessageResolver} 빈이 이미 존재하지 않을 것
 * </ul>
 *
 * <p>즉, Spring Boot의 “기본 제공(Default) / 사용자 정의 우선(Override)” 원칙을 따라, 필요한 경우에만 기본 예외 처리기와 메시지 해석기를
 * 자동으로 제공한다.
 *
 * @author 김지원
 * @since 0.2.0
 */
@AutoConfiguration
@Conditional(NoRestControllerAdviceCondition.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = "athenhub.exception.mvc",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class MvcExceptionHandlerAutoConfiguration {
  /**
   * 기본 MVC 예외 처리기를 등록한다.
   *
   * <p>사용자가 직접 {@link MvcExceptionHandler} 빈을 정의한 경우 이 빈은 생성되지 않으며, 사용자 정의 구현이 우선 적용된다.
   *
   * @param messageResolver 메시지 코드 해석기
   * @return 라이브러리에서 제공하는 기본 {@link MvcExceptionHandler}
   */
  @Bean
  @ConditionalOnMissingBean(MvcExceptionHandler.class)
  public MvcExceptionHandler mvcExceptionHandler(MessageResolver messageResolver) {
    return new MvcExceptionHandler(messageResolver);
  }

  /**
   * {@link MessageSource} 기반 메시지 해석기(MessageResolver)를 등록한다.
   *
   * <p>사용자가 직접 {@link MessageResolver} 빈을 제공하는 경우 이 기본 구현은 적용되지 않는다.
   *
   * @param messageSource Spring MessageSource (messages.properties 등)
   * @return 기본 메시지 해석기 {@link MessageSourceResolver}
   */
  @Bean
  @ConditionalOnMissingBean(MessageResolver.class)
  public MessageResolver messageResolver(MessageSource messageSource) {
    return new MessageSourceResolver(messageSource);
  }
}
