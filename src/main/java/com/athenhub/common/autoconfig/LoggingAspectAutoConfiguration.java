package com.athenhub.common.autoconfig;

import com.athenhub.common.filter.MdcFilter;
import com.athenhub.common.logging.LogManager;
import com.athenhub.common.logging.LoggingAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * AOP 기반의 LoggingAspect 및 MDC 필터를 자동 등록하는 AutoConfiguration.
 *
 * <p>이 설정은 다음 조건을 만족할 때 동작한다:
 *
 * <ul>
 *   <li>Servlet 기반 Web Application 일 경우
 *   <li>설정값 athenhub.logging.aspect.enabled=true (기본 true)
 * </ul>
 *
 * <p>사용자가 동일한 Bean 을 직접 등록한 경우 자동 등록되지 않는다.
 *
 * @author 김지원
 * @since 0.3.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = "athenhub.logging.mvc",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class LoggingAspectAutoConfiguration {

  /**
   * LoggingAspect 자동 등록.
   *
   * <p>사용자가 LoggingAspect 를 Bean 으로 직접 정의하지 않은 경우에만 등록된다. LogManager 를 기본 구현체로 사용한다.
   */
  @Bean
  @ConditionalOnMissingBean(LoggingAspect.class)
  public LoggingAspect loggingAspect() {
    return new LoggingAspect(new LogManager());
  }

  /**
   * 요청 단위로 MDC(traceId, requestId 등)를 설정하는 MdcFilter 등록.
   *
   * <p>사용자가 이미 동일한 타입의 Filter 를 제공한 경우 override 하지 않는다.
   */
  @Bean
  @ConditionalOnMissingBean
  public MdcFilter mdcFilter() {
    return new MdcFilter();
  }

  /**
   * 서블릿 필터로 MdcFilter 를 등록.
   *
   * <p>FilterRegistrationBean 을 통해 필터의 실행 순서를 지정한다. 다른 필터보다 먼저 실행되도록 높은 우선순위(HIGHEST_PRECEDENCE +
   * 10)를 부여한다.
   *
   * @param filter MdcFilter 빈
   * @return FilterRegistrationBean
   */
  @Bean
  public FilterRegistrationBean<MdcFilter> mdcFilterRegistration(MdcFilter filter) {
    FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10); // 요청 초기에 MDC 로깅 설정
    return registration;
  }
}
