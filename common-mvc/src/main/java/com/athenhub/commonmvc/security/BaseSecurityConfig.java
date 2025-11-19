package com.athenhub.commonmvc.security;

import com.athenhub.commonmvc.security.filter.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 공통적으로 적용할 Spring Security 보안 설정의 기반 클래스.
 *
 * <p>이 클래스는 템플릿 메서드 패턴(Template Method Pattern)을 기반으로 설계되어 있으며, 하위 보안 설정 클래스가 공통적인 필터 체인 구성 로직은 그대로
 * 사용하면서, 특정 지점(예: 로그인 필터 정의, 엔드포인트 접근 정책, 예외 처리)을 필요에 따라 확장할 수 있도록 한다.
 *
 * <h2>핵심 목적</h2>
 *
 * <ul>
 *   <li>Stateless 기반 JWT 인증 구조를 간단히 구성하기 위한 공통 보안 설정 제공
 *   <li>사용자 정의 {@link LoginFilter}를 필터 체인에 등록하기 위한 확장 포인트 제공
 *   <li>401(UNAUTHORIZED), 403(FORBIDDEN) 등 인증/인가 실패 상황에 대한 기본 예외 처리 제공
 *   <li>세션을 사용하지 않는 REST API 환경에 적합한 기본 정책 제공
 * </ul>
 *
 * <h2>확장 방식</h2>
 *
 * <p>하위 클래스는 반드시 {@link #loginFilterBean()}을 구현하여 실제 인증 로직을 수행할 커스텀 {@link LoginFilter} 인스턴스를 제공해야
 * 한다.
 *
 * <p>아래 메서드는 필요에 따라 재정의할 수 있다:
 *
 * <ul>
 *   <li>{@link #authorizeHttpRequests()} — 엔드포인트별 접근 제어 정책
 *   <li>{@link #exceptionHandlingCustomizer()} — 인증/인가 예외 처리 정책
 *   <li>{@link #authenticationEntryPoint()} — 인증 실패(401) 처리
 *   <li>{@link #accessDeniedHandler()} — 권한 부족(403) 처리
 * </ul>
 *
 * <h2>기본 제공 기능</h2>
 *
 * <ul>
 *   <li>CSRF 비활성화
 *   <li>세션 생성 정책을 {@link SessionCreationPolicy#STATELESS}로 설정
 *   <li>하위 클래스에서 제공한 {@link LoginFilter}를 {@link UsernamePasswordAuthenticationFilter} 이전에 필터 체인에
 *       삽입
 *   <li>기본적으로 모든 요청에 대해 인증 요구 (하위 클래스의 {@link #authorizeHttpRequests()} 재정의로 변경 가능)
 *   <li>401/403 상황에 대한 기본적인 HTTP 상태 코드 응답 처리
 * </ul>
 *
 * <h2>사용 예시</h2>
 *
 * <pre>{@code
 * @Configuration
 * public class CustomSecurityConfig extends BaseSecurityConfig {
 *     @Override
 *     protected LoginFilter loginFilterBean() {
 *         return new CustomLoginFilter(...);
 *     }
 *
 *     @Override
 *     protected Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>
 *             .AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequests() {
 *         return registry -> registry
 *             .requestMatchers("/public/**").permitAll()
 *             .anyRequest().authenticated();
 *     }
 * }
 * }</pre>
 *
 * <p>이 클래스를 상속하면 프로젝트 전반에서 일관된 보안 정책을 쉽게 유지할 수 있으며, 필요한 부분만 선택적으로 확장할 수 있다.
 *
 * @author 김형섭
 * @since 0.4.0
 */
public abstract class BaseSecurityConfig {

  /**
   * 로그인(인증) 처리를 담당하는 커스텀 {@link LoginFilter} 빈을 반환합니다.
   *
   * <p>하위 클래스는 이 메서드를 구현하여 실제 인증 로직을 수행하는 {@link LoginFilter} 인스턴스를 반환해야 합니다. 반환된 필터는 {@link
   * UsernamePasswordAuthenticationFilter} 앞에 등록됩니다.
   *
   * @return 인증 처리를 담당하는 {@link LoginFilter} 인스턴스
   */
  protected abstract LoginFilter loginFilterBean();

  /**
   * {@link HttpSecurity}를 사용하여 {@link SecurityFilterChain}을 구성하고 빌드합니다.
   *
   * <p>기본 구성은 다음을 포함합니다:
   *
   * <ul>
   *   <li>CSRF 비활성화
   *   <li>하위 클래스가 제공한 {@link LoginFilter}를 {@link UsernamePasswordAuthenticationFilter} 전에 등록
   *   <li>세션 생성 정책을 {@link SessionCreationPolicy#STATELESS}로 설정
   *   <li>인증/인가 관련 커스터마이저( {@link #authorizeHttpRequests()}, {@link #exceptionHandlingCustomizer()}
   *       ) 적용
   * </ul>
   *
   * <p>필요 시 하위 클래스에서 구성 메서드들을 오버라이드하여 정책을 변경할 수 있습니다.
   *
   * @param http {@link HttpSecurity} 구성 객체
   * @return 구성된 {@link SecurityFilterChain}
   * @throws Exception Spring Security 구성 중 발생할 수 있는 예외
   */
  public SecurityFilterChain build(HttpSecurity http) throws Exception {
    http.csrf(CsrfConfigurer::disable)
        .addFilterBefore(loginFilterBean(), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeHttpRequests())
        .exceptionHandling(exceptionHandlingCustomizer());

    return http.build();
  }

  /**
   * HTTP 요청별 권한(인가) 규칙을 커스터마이즈할 {@link Customizer}를 반환합니다.
   *
   * <p>기본 구현은 모든 요청에 대해 인증을 요구하도록 설정(`registry.anyRequest().authenticated()`)합니다. 엔드포인트별 접근 제어나 세부
   * 규칙이 필요하면 하위 클래스에서 이 메서드를 오버라이드하세요.
   *
   * @return {@link AuthorizeHttpRequestsConfigurer}의 설정을 커스터마이즈할 {@link Customizer}
   */
  protected Customizer<
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
      authorizeHttpRequests() {
    return registry -> registry.anyRequest().authenticated();
  }

  /**
   * 인증/인가 예외 처리(예: 401, 403) 동작을 커스터마이즈할 {@link Customizer}를 반환합니다.
   *
   * <p>기본 구현은 {@link #authenticationEntryPoint()}와 {@link #accessDeniedHandler()}를 사용하여 인증 실패 및 접근
   * 거부 상황에 대해 기본 응답을 지정합니다. 예외 처리 로직을 변경하려면 이 메서드를 오버라이드하세요.
   *
   * @return {@link ExceptionHandlingConfigurer} 구성을 위한 {@link Customizer}
   */
  protected Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer() {
    return handler -> {
      handler.authenticationEntryPoint(authenticationEntryPoint());
      handler.accessDeniedHandler(accessDeniedHandler());
    };
  }

  /**
   * 인증이 필요한 요청에서 인증이 되어있지 않을 때 호출되는 {@link AuthenticationEntryPoint}를 반환합니다.
   *
   * <p>기본 구현은 {@link HttpServletResponse#SC_UNAUTHORIZED} (401) 상태 코드를 전송합니다. 필요하면 하위 클래스에서 더 자세한
   * 응답(예: JSON 바디, 로깅 등)을 반환하도록 오버라이드하세요.
   *
   * @return 인증 실패 시 동작할 {@link AuthenticationEntryPoint}
   */
  protected AuthenticationEntryPoint authenticationEntryPoint() {
    return (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }

  /**
   * 인증은 되었지만 권한이 부족한 요청(접근 거부)에 대해 처리할 {@link AccessDeniedHandler}를 반환합니다.
   *
   * <p>기본 구현은 {@link HttpServletResponse#SC_UNAUTHORIZED} (401) 상태 코드를 전송합니다. (필요시 403으로 변경하거나,
   * JSON 형식 응답을 하려면 오버라이드하세요.)
   *
   * @return 접근 거부 시 동작할 {@link AccessDeniedHandler}
   */
  protected AccessDeniedHandler accessDeniedHandler() {
    return (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
