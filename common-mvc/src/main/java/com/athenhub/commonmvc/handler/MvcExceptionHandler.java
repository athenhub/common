package com.athenhub.commonmvc.handler;

import com.athenhub.commoncore.error.AbstractApplicationException;
import com.athenhub.commoncore.error.ErrorCode;
import com.athenhub.commoncore.error.ErrorResponse;
import com.athenhub.commoncore.error.FieldError;
import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.commoncore.message.MessageResolver;
import com.athenhub.commonmvc.handler.utils.ValidationErrorParser;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * MVC 환경에서 발생하는 예외를 공통으로 처리하는 글로벌 예외 처리기.
 *
 * <p>이 클래스는 {@link RestControllerAdvice} 로 등록되어 컨트롤러 전역에서 발생하는 예외를 일원화하여 처리한다.
 *
 * <p>예외 처리 시 다음 기준에 따라 동작한다:
 *
 * <ul>
 *   <li>{@link AbstractApplicationException} : 비즈니스 예외 → ErrorCode 기반 메시지 생성
 *   <li>{@link MethodArgumentNotValidException} : 검증 실패 예외 → VALIDATION_ERROR 사용
 *   <li>{@link Exception} : 처리되지 않은 모든 예외 → INTERNAL_SERVER_ERROR 처리
 * </ul>
 *
 * <p>또한 MessageResolver를 이용해 메시지 템플릿을 해석하여 클라이언트에게 직관적인 메시지를 제공한다.
 *
 * @author 김지원
 * @since 0.2.0
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class MvcExceptionHandler {

  private final MessageResolver messageResolver;

  /**
   * 비즈니스 예외(ApplicationException)를 처리한다.
   *
   * <p>커스텀 예외는 ErrorCode 및 ErrorArgs를 기반으로 메시지를 생성하며, 개발자가 전달한 직접 메시지가 있는 경우 그 메시지가 우선 적용된다.
   *
   * @param e 발생한 커스텀 애플리케이션 예외
   * @return ErrorResponse 형태의 HTTP 응답
   */
  @ExceptionHandler(value = AbstractApplicationException.class)
  public ResponseEntity<ErrorResponse<Void>> handleApplicationException(
      AbstractApplicationException e) {
    log.error("[{}]", e.getClass().getSimpleName(), e);

    String message =
        e.getMessage() == null
            ? messageResolver.resolve(e.getCode(), e.getErrorArgs())
            : e.getMessage();

    return ResponseEntity.status(e.getStatus()).body(ErrorResponse.of(e.getCode(), message));
  }

  /**
   * Bean Validation(@Valid) 검증 실패 시 발생하는 예외를 처리한다.
   *
   * <p>검증 오류 목록을 필드 단위/글로벌 단위로 추출하여 클라이언트에게 상세 오류 정보를 제공한다.
   *
   * @param e MethodArgumentNotValidException
   * @return {@code ErrorResponse(List<FieldError>)} 형태의 HTTP 응답
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse<List<FieldError>>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    List<FieldError> errors = ValidationErrorParser.from(e);

    ErrorCode errorCode = GlobalErrorCode.VALIDATION_ERROR;

    return ResponseEntity.status(errorCode.getStatus())
        .body(
            ErrorResponse.of(
                errorCode.getCode(), messageResolver.resolve(errorCode.getCode()), errors));
  }

  /**
   * 메서드 파라미터(@RequestParam, @PathVariable 등) 검증 실패 시 발생하는 예외를 처리한다.
   *
   * <p>Spring 6부터는 메서드 파라미터 수준의 검증이 강화되었으며, 해당 검증이 실패할 경우 {@link HandlerMethodValidationException}
   * 이 발생한다.
   *
   * <p>이 예외는 다음과 같은 경우에 발생한다:
   *
   * <ul>
   *   <li>@RequestParam 값이 Bean Validation 규칙을 위반한 경우
   *   <li>@PathVariable 값이 제약 조건을 만족하지 않는 경우
   *   <li>@RequestHeader 등 메서드 인자 검증이 실패한 경우
   * </ul>
   *
   * <p>본 메서드는 이 정보를 {@link FieldError} 리스트로 변환해 클라이언트에게 일관된 검증 오류 응답을 제공한다.
   *
   * @param e HandlerMethodValidationException
   * @return {@code ErrorResponse<List<FieldError>>} 형태의 HTTP 400 응답
   */
  @ExceptionHandler(value = HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse<List<FieldError>>> handleHandlerMethodValidationException(
      HandlerMethodValidationException e) {
    List<FieldError> errors = ValidationErrorParser.from(e);

    ErrorCode errorCode = GlobalErrorCode.VALIDATION_ERROR;

    return ResponseEntity.status(errorCode.getStatus())
        .body(
            ErrorResponse.of(
                errorCode.getCode(), messageResolver.resolve(errorCode.getCode()), errors));
  }

  /**
   * 지원되지 않는 HTTP 메서드(405 Method Not Allowed)에 대한 처리기.
   *
   * <p>Spring MVC는 컨트롤러에서 허용되지 않은 HTTP 메서드로 요청이 들어올 경우 {@link
   * HttpRequestMethodNotSupportedException} 을 발생시킨다.
   *
   * <p>이 핸들러는 예외로부터 지원되는 메서드 목록을 추출하여 메시지 템플릿에 삽입한 후, 글로벌 에러 코드 {@code METHOD_NOT_ALLOWED}와 함께 표준
   * {@link ErrorResponse} 형태로 변환한다.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse<Void>> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException e) {
    ErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;
    String message =
        messageResolver.resolve(
            errorCode.getCode(),
            String.join(", ", Objects.requireNonNull(e.getSupportedMethods())));

    return ResponseEntity.status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode.getCode(), message));
  }

  /**
   * 요청 본문(JSON)을 읽을 수 없을 때 발생하는 예외 처리기.
   *
   * <p>요청 JSON 형식 오류, 누락된 body, 타입 불일치 등으로 인해 {@link HttpMessageNotReadableException}이 발생할 수 있다.
   *
   * <p>전역 에러 코드 {@code INVALID_JSON}에 매핑하여 메시지 리졸버를 통해 사용자 친화적 문구로 변환한 뒤 표준 ErrorResponse 형태로 감싼다.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse<Void>> handleJsonParse(HttpMessageNotReadableException e) {
    ErrorCode errorCode = GlobalErrorCode.INVALID_JSON;

    return ResponseEntity.status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode.getCode(), messageResolver.resolve(errorCode.getCode())));
  }

  /**
   * 파라미터 타입 변환 실패(TypeMismatch) 예외 처리기.
   *
   * <p>예: "/users?id=abc" 요청 시 id(Long)가 "abc"로 변환될 수 없어 {@link
   * MethodArgumentTypeMismatchException}이 발생한다.
   *
   * <p>전역 에러 코드 {@code TYPE_MISMATCH}에 매핑하며, 메시지 템플릿에서 {0}=파라미터명, {1}=입력값 을 치환하여 사용자 안내 메시지를 생성한다.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse<Void>> handleTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    ErrorCode errorCode = GlobalErrorCode.TYPE_MISMATCH;
    ErrorResponse<Void> body =
        ErrorResponse.of(
            errorCode.getCode(),
            messageResolver.resolve(errorCode.getCode(), e.getName(), e.getValue()));

    return ResponseEntity.status(errorCode.getStatus()).body(body);
  }

  /**
   * 처리되지 않은 모든 예외를 잡아 500 INTERNAL_SERVER_ERROR로 응답한다.
   *
   * <p>예상하지 못한 런타임 오류를 공통 응답 포맷으로 반환하여 API 일관성을 유지한다.
   *
   * @param e 발생한 예외 객체
   * @return INTERNAL_SERVER_ERROR 응답
   */
  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse<Void>> handleAllUncaughtException(Exception e) {
    log.error("[{}]", e.getClass().getSimpleName(), e);

    GlobalErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

    return ResponseEntity.status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode.getCode(), messageResolver.resolve(errorCode.getCode())));
  }
}
