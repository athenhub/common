package com.athenhub.common.error;

import org.springframework.http.HttpStatusCode;

/**
 * 애플리케이션 전역에서 사용되는 공통 에러 코드를 정의하는 인터페이스.
 *
 * <p>각 도메인은 이 인터페이스를 구현한 열거형(enum)을 통해 에러 식별자(code)와 HTTP 상태(status)를 명시적으로 선언한다. 예외 발생 시 {@link
 * AbstractApplicationException}에서 이 정보를 활용해 응답 상태 코드와 메시지 변환을 일관성 있게 처리한다.
 *
 * <p>일반적인 사용 예:
 *
 * <pre>{@code
 * public enum UserErrorCode implements ErrorCode {
 *     USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"),
 *     INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD");
 *
 *     private final HttpStatusCode status;
 *     private final String code;
 *
 *     // 생성자 생략
 *
 *     @Override
 *     public HttpStatusCode getStatus() { return status; }
 *
 *     @Override
 *     public String getCode() { return code; }
 * }
 *
 * // 예외 선언
 * throw new UserNotFoundException(UserErrorCode.USER_NOT_FOUND, userId);
 * }</pre>
 *
 * <p>핸들러에서는 {@code getStatus()} 값으로 HTTP 응답 상태를 결정하고, {@code getCode()} 값으로 메시지 리졸버가 사용할 메시지 키를
 * 제공한다.
 *
 * @author 김지원
 * @since 0.2.0
 */
public interface ErrorCode {

  /**
   * 이 에러 코드에 매핑되는 HTTP 상태를 반환한다.
   *
   * @return HTTP 상태 코드
   */
  HttpStatusCode getStatus();

  /**
   * 에러를 식별하는 문자열 코드를 반환한다.
   *
   * <p>예: {@code "USER_NOT_FOUND"}, {@code "INVALID_TOKEN"}
   *
   * @return 에러 식별자 코드
   */
  String getCode();
}
