package com.athenhub.commonmvc.autoconfig;

import com.athenhub.commonmvc.swagger.SwaggerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Swagger 관련 기본 설정을 자동으로 등록하는 AutoConfiguration.
 *
 * <p>이 구성은 다음 조건을 모두 충족할 때만 활성화된다.
 *
 * <ul>
 *   <li>현재 애플리케이션이 Servlet 기반 Web MVC 환경일 것.
 *   <li><code>athenhub.swagger.config.enabled=true</code> 이거나 설정이 없을 것.
 *   <li>사용자가 동일 타입의 Swagger 설정 Bean을 직접 정의하지 않았을 것.
 * </ul>
 *
 * <p>즉, Spring Boot의 “기본 제공(Default) / 사용자 정의 우선(Override)” 원칙에 따라, 필요한 경우에만 기본 Swagger 설정을 자동으로
 * 제공한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = "athenhub.swagger.config",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SwaggerAutoConfiguration {

  /**
   * 기본 Swagger 설정 Bean을 등록한다.
   *
   * <p>사용자가 직접 {@link SwaggerConfig} Bean을 정의한 경우 해당 Bean이 우선 적용되며, 이 기본 Bean은 생성되지 않는다.
   *
   * @return 기본 {@link SwaggerConfig}
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  @ConditionalOnMissingBean
  public SwaggerConfig swaggerConfig() {
    return new SwaggerConfig();
  }
}
