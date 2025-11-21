package com.athenhub.commonmvc.handler;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;
import com.athenhub.commoncore.error.GlobalErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트 환경에서 다양한 예외 상황을 발생시키기 위한 테스트 컨트롤러.
 *
 * <p>이 컨트롤러는 {@link com.athenhub.commonmvc.handler.MvcExceptionHandler} 및 공통 예외 처리 흐름을 검증하기 위해
 * 설계되었으며, 각 요청은 의도적으로 검증 오류, 타입 변환 실패, 비즈니스 예외, 런타임 예외 등을 발생시킨다.
 *
 * <p>주요 테스트 시나리오:
 *
 * <ul>
 *   <li>사용자 정의 ApplicationException 발생
 *   <li>커스텀 메시지를 가진 ApplicationException 발생
 *   <li>요청 본문(body) Bean Validation 실패
 *   <li>PathVariable / RequestParam 검증 실패
 *   <li>JSON 파싱 실패
 *   <li>타입 변환(TypeMismatch) 오류
 *   <li>지원되지 않는 HTTP Method 요청
 * </ul>
 */
@RestController
@RequestMapping("/test")
public class TestController {

  /** GlobalErrorCode.NOT_FOUND 기반의 ApplicationException 발생. */
  @GetMapping("/app-ex")
  public void throwAppException() {
    throw new TestApplicationException(GlobalErrorCode.NOT_FOUND);
  }

  /** MessageResolver를 사용하지 않는 커스텀 메시지를 포함한 ApplicationException 발생. */
  @GetMapping("/app-ex-custom")
  public void throwAppExceptionWithCustomExceptionMessage() {
    throw new TestApplicationException(
        GlobalErrorCode.NOT_FOUND, "MessageResolver를 사용하지 않은 커스텀 메세지");
  }

  /**
   * 요청 본문 Bean Validation 실패 테스트용.
   *
   * @param request 이름/나이 필드 검증이 포함된 요청 객체
   */
  @PostMapping("/invalid-request-body")
  public void throwInvalidRequestBodyException(@Valid @RequestBody PersonRequest request) {}

  /**
   * PathVariable 검증 실패 테스트.
   *
   * @param id 공백이면 검증 실패
   */
  @GetMapping("/invalid-path-variable/{id}")
  public void throwInvalidVariableException(
      @Valid @NotBlank @PathVariable(name = "id") String id) {}

  /**
   * RequestParam 검증 실패 테스트.
   *
   * @param id 공백 전달 시 Bean Validation 실패
   */
  @GetMapping("/invalid-request-parm")
  public void throwInvalidParamException(@Valid @NotBlank @RequestParam(name = "id") String id) {}

  /** 단순 RuntimeException 발생. */
  @GetMapping("/ex")
  public void throwException() {
    throw new RuntimeException("boom");
  }

  /**
   * POST 요청만 허용. GET 요청 등 잘못된 Method 호출 시 405 MethodNotAllowed 테스트 가능.
   *
   * @return "ok"
   */
  @PostMapping("/invalid-method")
  public String postOnly() {
    return "ok";
  }

  /**
   * JSON 파싱 오류를 유도한다.
   *
   * @param request JSON 파싱 실패 시 {@link
   *     org.springframework.http.converter.HttpMessageNotReadableException} 발생
   * @return "ok"
   */
  @PostMapping("/invalid-json")
  public String jsonParseException(@RequestBody PersonRequest request) {
    return "ok";
  }

  /**
   * PathVariable 타입 불일치(TypeMismatch) 테스트. Long 타입에 숫자가 아닌 문자열 전달 시 예외 발생.
   *
   * @param id Long 타입 PathVariable
   * @return "ok"
   */
  @GetMapping("/mismatch/{id}")
  public String typeMismatchException(@PathVariable Long id) {
    return "ok";
  }

  /**
   * 권한 체크 테스트용 엔드포인트.
   *
   * <p>{@code ROLE_MANAGER} 역할을 가진 사용자만 접근할 수 있도록 설정된 API이다. Spring Security의 권한 검증(@PreAuthorize)
   * 동작을 확인하기 위한 목적으로 사용된다.
   *
   * <p>정상적으로 권한이 부여된 경우 단순 문자열 "ok"를 반환한다.
   *
   * @return "ok" (테스트 응답)
   */
  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/access-denial")
  public String accessDenialException() {
    return "ok";
  }

  /**
   * 테스트용 사용자 정의 ApplicationException.
   *
   * <p>공통 예외 처리기가 이 예외를 잡아 ErrorResponse로 변환하는지 검증한다.
   */
  static class TestApplicationException extends AbstractServiceException {

    /**
     * 테스트용 기본 생성자.
     *
     * @param errorCode 공통 에러 코드
     * @param errorArgs 메시지 포맷용 변수들
     */
    public TestApplicationException(ErrorCode errorCode, Object... errorArgs) {
      super(errorCode, errorArgs);
    }

    /**
     * 커스텀 메시지를 지정할 수 있는 생성자.
     *
     * @param errorCode 공통 에러 코드
     * @param message 사용자 정의 메시지 (MessageResolver 미사용)
     * @param errorArgs 메시지 포맷용 변수들
     */
    public TestApplicationException(ErrorCode errorCode, String message, Object... errorArgs) {
      super(errorCode, message, errorArgs);
    }
  }

  /**
   * 요청 본문 검증 테스트용 DTO.
   *
   * @param name 공백 불가
   * @param age 1 이상
   */
  public record PersonRequest(@NotBlank String name, @Min(1) int age) {}
}
