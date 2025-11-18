package com.athenhub.commonmvc.filter;

import com.athenhub.commonmvc.utils.MdcUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 요청 단위로 MDC(Context Map)를 초기화하고 관리하는 필터.
 *
 * <p>이 필터는 각 HTTP 요청마다 고유한 requestId를 생성해 MDC에 저장하고, Spring Security 인증 정보가 존재할 경우 사용자명을 MDC에 함께
 * 저장합니다. 로깅 시 requestId 및 username을 자동으로 포함할 수 있어 로그 추적성(Traceability)을 향상시키는 데 사용됩니다.
 *
 * <p>{@link OncePerRequestFilter}를 상속하여 요청당 한 번만 실행되며, 요청 처리가 완료된 후에는 MDC를 반드시 초기화하여 메모리 누수 및 정보
 * 오염을 방지합니다.
 *
 * @author 김형섭
 * @since 0.3.0
 */
@Component
public class MdcFilter extends OncePerRequestFilter {

  /**
   * 요청마다 requestId와 인증 사용자명을 MDC에 저장하고, 요청 처리가 끝나면 MDC를 초기화한다.
   *
   * @param request HTTP 요청
   * @param response HTTP 응답
   * @param filterChain 필터 체인
   * @throws ServletException 필터 처리 중 서블릿 예외 발생 시
   * @throws IOException 필터 처리 중 I/O 예외 발생 시
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestId = UUID.randomUUID().toString();
    MdcUtils.setRequestId(requestId);
    MdcUtils.setRequestUsername(getUsername(request));

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  /**
   * 현재 RequestContextHolder에서 인증된 사용자명을 조회한다. 인증 정보가 없을 경우 "SYSTEM"를 반환한다.
   *
   * @return 인증된 사용자명 또는 기본값 "SYSTEM"
   */
  private String getUsername(HttpServletRequest request) {
    String username = request.getHeader("X-User-Name");
    return username.isBlank() ? "SYSTEM" : username;
  }
}
