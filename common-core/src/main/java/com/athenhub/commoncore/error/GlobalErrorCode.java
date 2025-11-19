package com.athenhub.commoncore.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션 공통 범위에서 발생하는 표준 에러 코드를 정의하는 열거형(enum).
 *
 * <p>도메인에 종속되지 않는 전역 오류(요청 형식 오류, 인증 실패, 권한 부족 등)에 대해 일관된 HTTP 상태와 에러 식별자를 제공한다. 각 항목은 {@link
 * ErrorCode} 인터페이스를 구현하며 전역 예외 처리기에서 응답 상태코드와 메시지 변환에 활용된다.
 *
 * <p>일반적인 사용 예:
 *
 * <pre>{@code
 * if (!tokenProvider.isValid(token)) {
 *     throw new AuthenticationException(GlobalErrorCode.UNAUTHORIZED);
 * }
 *
 * // 예외 처리기 내부 (예: MvcExceptionHandler)
 * @ExceptionHandler(AuthenticationException.class)
 * public ResponseEntity<ErrorResponse<Void>> handle(AuthenticationException ex) {
 *     String message = messageResolver.resolve(ex.getCode(), ex.getErrorArgs());
 *     return ResponseEntity
 *             .status(ex.getStatus())
 *             .body(ErrorResponse.of(ex.getCode(), message));
 * }
 * }</pre>
 *
 * <p>각 에러 코드는 다음 구조를 가진다:
 *
 * <ul>
 *   <li>{@code status}: HTTP 상태 코드
 *   <li>{@code code}: 전역 에러 식별자 문자열
 * </ul>
 *
 * @author 김지원
 * @since 0.2.0
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

  /** 잘못된 요청 형식 또는 유효하지 않은 입력으로 인해 요청을 처리할 수 없는 경우. 예: JSON 파싱 오류, 필수 파라미터 누락 */
  BAD_REQUEST(400, "BAD_REQUEST"),

  /** 입력 값 검증(Validation) 실패. 예: @Valid, @NotNull, @Email 등 제약조건 위반 */
  VALIDATION_ERROR(400, "VALIDATION_ERROR"),

  /** 요청 본문(JSON) 파싱 실패 또는 잘못된 형식일 때 발생하는 에러 코드 (400 Bad Request). */
  INVALID_JSON(400, "INVALID_JSON"),

  /** 요청 파라미터 타입 변환에 실패했을 때 발생하는 에러 코드 (400 Bad Request). */
  TYPE_MISMATCH(400, "TYPE_MISMATCH"),

  /** 인증되지 않은 사용자가 보호된 리소스에 접근하려는 경우. */
  UNAUTHORIZED(401, "UNAUTHORIZED"),

  /** 인증은 되었지만 리소스 접근 권한이 부족한 경우. */
  FORBIDDEN(403, "FORBIDDEN"),

  /** 요청한 리소스를 찾을 수 없는 경우. */
  NOT_FOUND(404, "NOT_FOUND"),

  /** 지원되지 않는 HTTP 메서드 요청 시 반환되는 에러 코드 (405 Method Not Allowed). */
  METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED"),

  /** 서버 내부 처리 중 예상치 못한 오류가 발생한 경우. */
  INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

  /** HTTP 상태 코드. */
  private final int status;

  /** 전역 에러 식별자 문자열. */
  private final String code;
}
