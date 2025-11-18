package com.athenhub.commoncore.error;

import lombok.Getter;

/**
 * API 에러 응답의 표준 형태를 표현하는 클래스.
 *
 * <p>모든 예외 처리 결과는 이 객체로 변환되어 클라이언트에 반환된다. 필드는 다음과 같은 의미를 가진다:
 *
 * <ul>
 *   <li>{@code code}: 애플리케이션 에러 코드 (예: {@code USER_NOT_FOUND})
 *   <li>{@code message}: 최종 사용자에게 전달할 메시지
 *   <li>{@code details}: 선택적 추가 정보 (검증 오류 목록 등)
 * </ul>
 *
 * <p>일반적인 사용 예:
 *
 * <pre>{@code
 * // 예외 핸들러 내부 (MvcExceptionHandler)
 * @ExceptionHandler(UserNotFoundException.class)
 * public ResponseEntity<ErrorResponse<Void>> handle(UserNotFoundException ex) {
 *     String message = messageResolver.resolve(ex.getCode(), ex.getErrorArgs());
 *     return ResponseEntity
 *             .status(ex.getStatus().value())
 *             .body(ErrorResponse.of(ex.getCode(), message));
 * }
 * }</pre>
 *
 * <p>검증 오류처럼 추가 정보를 제공해야 하는 경우:
 *
 * <pre>{@code
 * Map<String, String> errors = Map.of("email", "형식이 올바르지 않습니다.");
 *
 * ErrorResponse<Map<String, String>> response =
 *         ErrorResponse.of("INVALID_INPUT", "입력 값이 올바르지 않습니다.", errors);
 * }</pre>
 *
 * @param <T> details 필드에 포함될 추가 데이터의 타입
 * @author 김지원
 * @since 0.2.0
 */
@Getter
public class ErrorResponse<T> {

  /**
   * 애플리케이션 에러 코드.
   *
   * <p>예: {@code USER_NOT_FOUND}, {@code INVALID_TOKEN}
   */
  private final String code;

  /** 사용자에게 전달되는 최종 메시지. */
  private final String message;

  /**
   * 선택적으로 포함되는 추가 정보.
   *
   * <p>예: 필드 검증 오류 목록, 데이터 객체 등
   */
  private final T details;

  private ErrorResponse(String code, String message, T details) {
    this.code = code;
    this.message = message;
    this.details = details;
  }

  /**
   * 추가 정보 없이 에러 응답을 생성한다.
   *
   * @param code 에러 코드
   * @param message 사용자 메시지
   * @param <T> details 타입
   * @return {@code details}가 포함되지 않은 기본 오류 응답
   */
  public static <T> ErrorResponse<T> of(String code, String message) {
    return new ErrorResponse<>(code, message, null);
  }

  /**
   * 추가 정보를 포함하여 에러 응답을 생성한다.
   *
   * @param code 에러 코드
   * @param message 사용자 메시지
   * @param details 추가 정보 객체
   * @param <T> details 타입
   * @return details가 포함된 오류 응답
   */
  public static <T> ErrorResponse<T> of(String code, String message, T details) {
    return new ErrorResponse<>(code, message, details);
  }
}
