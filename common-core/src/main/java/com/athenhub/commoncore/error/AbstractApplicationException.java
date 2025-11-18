package com.athenhub.commoncore.error;

/**
 * 공통 애플리케이션 예외의 추상 클래스.
 *
 * <p>도메인별 예외 클래스는 이 클래스를 상속받아 사용하며, {@link ErrorCode}를 통해 에러 코드와 HTTP 상태를 포함한다. 또한 메시지 포맷에 필요한
 * 인자(messageArgs)를 함께 전달하여 메시지 파일(MessageSource)에서 최종 사용자 메시지를 생성할 수 있도록 한다.
 *
 * <p>핸들러({@code @RestControllerAdvice})에서는 {@link #getCode()} 및 {@link #getErrorArgs()}를 이용해 메시지를
 * 조회하고 응답 본문을 구성한다.
 *
 * <pre>{@code
 * throw new UserNotFoundException(USER_NOT_FOUND, userId);
 * }</pre>
 *
 * @author 김지원
 * @since 0.2.0
 */
public abstract class AbstractApplicationException extends RuntimeException {

  /** 에러 식별자 및 HTTP 상태를 포함한 공통 에러 코드. */
  private final ErrorCode errorCode;

  /**
   * 메시지 포맷 치환에 사용될 인자 목록.
   *
   * <p>예: messages.properties 내 문자열 {@code "USER_NOT_FOUND=사용자 {0}을 찾을 수 없습니다."} 와 함께 사용됨.
   */
  private final Object[] errorArgs;

  /**
   * 에러 코드와 메시지 인자를 포함한 기본 생성자.
   *
   * @param errorCode 에러 코드
   * @param errorArgs 메시지 포맷 인자
   */
  public AbstractApplicationException(ErrorCode errorCode, Object... errorArgs) {
    this.errorCode = errorCode;
    this.errorArgs = errorArgs;
  }

  /**
   * 에러 코드와 사용자 정의 메시지를 포함한 생성자.
   *
   * @param errorCode 에러 코드
   * @param message 사용자 정의 메시지 (MessageSource 우선 적용)
   * @param errorArgs 메시지 포맷 인자
   */
  public AbstractApplicationException(ErrorCode errorCode, String message, Object... errorArgs) {
    super(message);
    this.errorCode = errorCode;
    this.errorArgs = errorArgs;
  }

  /**
   * 에러 코드, 메시지, 원인(cause)을 포함한 생성자.
   *
   * @param errorCode 에러 코드
   * @param message 사용자 정의 메시지
   * @param cause 발생 원인
   * @param errorArgs 메시지 포맷 인자
   */
  public AbstractApplicationException(
      ErrorCode errorCode, String message, Throwable cause, Object... errorArgs) {
    super(message, cause);
    this.errorCode = errorCode;
    this.errorArgs = errorArgs;
  }

  /**
   * 에러 코드와 발생 원인을 포함한 생성자.
   *
   * @param errorCode 에러 코드
   * @param cause 발생 원인
   * @param errorArgs 메시지 포맷 인자
   */
  public AbstractApplicationException(ErrorCode errorCode, Throwable cause, Object... errorArgs) {
    super(cause);
    this.errorCode = errorCode;
    this.errorArgs = errorArgs;
  }

  /**
   * HTTP 상태 코드를 반환한다.
   *
   * @return HTTP 상태
   */
  public int getStatus() {
    return errorCode.getStatus();
  }

  /**
   * 에러 코드를 문자열로 반환한다.
   *
   * <p>예: USER_NOT_FOUND, INVALID_PASSWORD 등
   *
   * @return 에러 식별자 문자열
   */
  public String getCode() {
    return errorCode.getCode();
  }

  /**
   * 메시지 포맷에 사용될 인자 배열을 반환한다.
   *
   * <p>예외 처리 시 {@link com.athenhub.commoncore.message.MessageResolver}를 통해 최종 메시지를 생성하는 데 사용된다.
   *
   * @return 메시지 치환용 인자 배열
   */
  public Object[] getErrorArgs() {
    return errorArgs;
  }
}
