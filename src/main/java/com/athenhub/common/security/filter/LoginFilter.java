package com.athenhub.common.security.filter;

import com.athenhub.common.security.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 사용자 인증 정보를 HTTP Header에서 읽어 Spring Security의 인증 컨텍스트에 설정하는 필터.
 *
 * <p>이 필터는 외부 인증 시스템 또는 API Gateway에서 전달한 사용자 정보를 기반으로 애플리케이션 내부에서 인증된 사용자처럼 동작할 수 있도록 구성하기 위해
 * 사용됩니다. 인증 정보는 SecurityContext에 저장되며 이후 컨트롤러, 서비스 레이어에서 {@code Authentication} 또는
 * {@code @AuthenticationPrincipal}을 통해 접근할 수 있습니다.
 *
 * <p>필터는 다음과 같은 헤더 정보를 기반으로 인증 객체를 생성합니다:
 *
 * <ul>
 *   <li>{@code X-User-Id} — 사용자 UUID
 *   <li>{@code X-Username} — 로그인 ID
 *   <li>{@code X-User-Name} — 사용자 실명 (URL 디코딩 처리)
 *   <li>{@code X-Slack-Id} — 사용자 Slack ID
 *   <li>{@code X-User-Roles} — 사용자 역할 목록
 * </ul>
 *
 * <p>{@code X-User-Id} 또는 {@code X-Username}이 누락된 경우 인증 처리는 수행되지 않으며, 필터는 다음 체인으로 요청을 그대로 전달합니다.
 *
 * @author 김형섭
 * @since 1.0.0
 */
@Component
public class LoginFilter extends GenericFilterBean {

  private static final String HEADER_USER_ID = "X-User-Id";
  private static final String HEADER_USERNAME = "X-Username";
  private static final String HEADER_USER_NAME = "X-User-Name";
  private static final String HEADER_SLACK_ID = "X-Slack-Id";
  private static final String HEADER_ROLES = "X-User-Roles";

  /**
   * 요청에서 사용자 인증 정보를 추출하여 SecurityContext에 저장하고, 다음 필터로 요청을 전달한다.
   *
   * @param request 필터 요청 객체
   * @param response 필터 응답 객체
   * @param filterchain 필터 체인
   * @throws IOException 입출력 예외 발생 시
   * @throws ServletException 요청 처리 중 서블릿 예외 발생 시
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
      throws IOException, ServletException {
    doLogin((HttpServletRequest) request);

    filterchain.doFilter(request, response);
  }

  /**
   * HTTP Header에서 사용자 정보를 추출하여 Authentication 객체로 변환하고 Spring Security의 SecurityContext에 설정한다.
   *
   * <p>{@code X-User-Id} 또는 {@code X-Username} 값이 없을 경우 인증 처리는 건너뛴다.
   *
   * @param request 현재 HTTP 요청
   */
  private void doLogin(HttpServletRequest request) {
    String id = request.getHeader(HEADER_USER_ID);
    String username = request.getHeader(HEADER_USERNAME);

    if (!StringUtils.hasText(id) || !StringUtils.hasText(username)) {
      return;
    }

    String name =
        request.getHeader(HEADER_USER_NAME) == null
            ? null
            : URLDecoder.decode(request.getHeader(HEADER_USER_NAME), StandardCharsets.UTF_8);
    String slackId = request.getHeader(HEADER_SLACK_ID);
    String roles = request.getHeader(HEADER_ROLES);

    UserDetails userDetails =
        AuthenticatedUser.of(UUID.fromString(id), username, name, slackId, roles);

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
