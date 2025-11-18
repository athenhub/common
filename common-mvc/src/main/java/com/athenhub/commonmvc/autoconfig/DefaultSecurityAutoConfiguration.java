package com.athenhub.commonmvc.autoconfig;

import com.athenhub.commonmvc.security.BaseSecurityConfig;
import com.athenhub.commonmvc.security.filter.LoginFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Boot 자동 구성 기반으로 기본 보안 설정을 제공하는 Auto-Configuration 클래스.
 *
 * <p>이 클래스는 {@link BaseSecurityConfig}를 확장하여 최소한의 기본 Security 구성을 자동으로 적용합니다. 애플리케이션이 별도의 {@link
 * SecurityFilterChain} 빈을 정의하지 않은 경우에만 활성화되며, Servlet 기반 WebApplication 환경에서 조건적으로 등록됩니다.
 *
 * <h2>기능 및 역할</h2>
 *
 * <ul>
 *   <li>기본 {@link LoginFilter} 빈 자동 등록
 *   <li>사용자 정의 보안 설정(SecurityFilterChain)이 없는 경우 기본 보안 정책 자동 적용
 *   <li>{@link BaseSecurityConfig#build(HttpSecurity)} 를 활용한 공통 보안 설정 구성
 *   <li>{@link EnableMethodSecurity}를 통해 메서드 기반 보안(@PreAuthorize 등) 기본 활성화 <br>
 *       ※ 사용자가 별도 SecurityConfig에서 @EnableMethodSecurity를 적용하면 해당 설정이 우선됩니다.
 * </ul>
 *
 * <h2>Auto-Configuration 적용 조건</h2>
 *
 * <ul>
 *   <li>{@link SecurityFilterChain} 클래스 존재 시 적용
 *   <li>사용자가 {@link SecurityFilterChain} 빈을 정의하지 않은 경우에만 적용
 *   <li>Servlet 기반 WebApplication일 경우에 한해 활성화
 * </ul>
 *
 * <p>사용자는 별도의 SecurityConfig를 정의하면 자동 구성은 비활성화되며, 원하는 방식으로 보안 설정을 완전히 변경할 수 있습니다.
 *
 * <p>또한 필요할 경우 {@link BaseSecurityConfig}의 확장 지점 (예: {@link
 * BaseSecurityConfig#authorizeHttpRequests()}, {@link
 * BaseSecurityConfig#exceptionHandlingCustomizer()})을 override하여 접근 제어 정책이나 예외 처리 등을 세부적으로 재정의할 수
 * 있습니다.
 *
 * @author 김형섭
 * @since 0.4.0
 */
@AutoConfiguration
@EnableMethodSecurity
@ConditionalOnClass(SecurityFilterChain.class)
@ConditionalOnMissingBean(SecurityFilterChain.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class DefaultSecurityAutoConfiguration extends BaseSecurityConfig {

  /**
   * 기본 인증 필터인 {@link LoginFilter} 빈을 정의합니다.
   *
   * <p>{@link ConditionalOnMissingBean}을 사용하기 때문에 사용자가 동일한 타입의 빈을 정의하면 이 빈은 생성되지 않습니다.
   *
   * @return 기본 {@link LoginFilter} 인스턴스
   */
  @Bean
  @ConditionalOnMissingBean
  public LoginFilter loginFilter() {
    return new LoginFilter();
  }

  /**
   * {@link BaseSecurityConfig}에서 요구하는 로그인 필터를 반환합니다.
   *
   * <p>상위 클래스는 loginFilterBean()을 통해 필터 체인 구성 시 로그인 필터를 가져가므로, 기본적으로 여기서는 자동 구성된 {@link
   * #loginFilter()}를 반환합니다.
   *
   * @return 자동 구성된 {@link LoginFilter} 빈
   */
  @Override
  protected LoginFilter loginFilterBean() {
    return loginFilter();
  }

  /**
   * 애플리케이션 기본 보안 필터 체인을 구성하여 빈으로 등록합니다.
   *
   * <p>이 빈은 사용자가 별도로 {@link SecurityFilterChain} 빈을 제공하지 않았을 때만 등록됩니다. 보안 체인의 내용은 {@link
   * BaseSecurityConfig#build(HttpSecurity)}에서 정의한 공통 정책(Stateless, CSRF 비활성화, 필터 등록 등)에 의해 구성됩니다.
   *
   * @param http {@link HttpSecurity} 보안 구성 객체
   * @return 기본 {@link SecurityFilterChain}
   * @throws Exception 보안 구성 중 발생할 수 있는 예외
   */
  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    return build(http);
  }
}
