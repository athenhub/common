package com.athenhub.common.logging;

import com.athenhub.common.utils.MdcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 로깅 기능을 중앙에서 관리하는 컴포넌트 클래스.
 *
 * <pre>
 * - RestController 요청 진입/종료 시점 로그 기록
 * - @LogExecution 애노테이션 적용 메서드 진입/종료 시점 로그 기록
 * - 예외 발생 시 MDC 정보와 함께 에러 로그 기록
 * - MDCUtils를 통해 요청 ID 및 인증 계정 정보 포함
 * </pre>
 *
 * @author 김형섭
 * @since 0.3.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogManager {
  /**
   * 컨트롤러 진입 시점에 HTTP 메서드, URI, 메서드 정보 및 추가 메시지를 INFO 레벨로 기록한다.
   *
   * @param httpMethod HTTP 메서드 (GET, POST 등)
   * @param requestUri 요청 URI
   * @param methodInfo 호출된 메서드 정보 (ClassName.methodName)
   * @param logMessage 추가 로그 메시지 (파라미터 정보 등)
   */
  public void logControllerEntry(
      final String httpMethod,
      final String requestUri,
      final String methodInfo,
      final String logMessage) {

    log.info("{} {}", formLogMessage(httpMethod, requestUri, methodInfo), logMessage);
  }

  /**
   * 컨트롤러 종료 시점에 HTTP 메서드, URI, 메서드 정보 및 반환 결과를 INFO 레벨로 기록한다.
   *
   * @param httpMethod HTTP 메서드 (GET, POST 등)
   * @param requestUri 요청 URI
   * @param methodInfo 호출된 메서드 정보 (ClassName.methodName)
   * @param resultJson 반환된 결과(JSON 또는 클래스명)
   */
  public void logControllerExit(
      final String httpMethod,
      final String requestUri,
      final String methodInfo,
      final String resultJson) {
    log.info("{}, Return: {}", formLogMessage(httpMethod, requestUri, methodInfo), resultJson);
  }

  /**
   * 예외 발생 시 MDC에 저장된 요청 ID와 계정 정보를 함께 ERROR 레벨로 기록한다.
   *
   * @param e 처리된 예외 객체
   */
  public void logException(final Exception e) {
    log.error(
        "Request ID: {}, Username: {}", MdcUtils.getRequestId(), MdcUtils.getRequestUsername(), e);
  }

  // 내부 로그 메시지 포맷팅 헬퍼 메서드 (private)
  private String formLogMessage(
      final String httpMethod, final String requestUri, final String methodInfo) {
    return String.format("%s %s - %s", httpMethod, requestUri, formCoreLogMessage(methodInfo));
  }

  // 내부 공통 로그 메시지 포맷팅 헬퍼 메서드 (private)
  private String formCoreLogMessage(final String methodInfo) {
    return String.format(
        "Request ID: %s, Username: %s, Method: %s",
        MdcUtils.getRequestId(), MdcUtils.getRequestUsername(), methodInfo);
  }
}
