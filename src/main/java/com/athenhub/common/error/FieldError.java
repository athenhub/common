package com.athenhub.common.error;

import lombok.Getter;

/**
 * 단일 필드 또는 전역 수준의 입력 오류를 표현하는 모델.
 *
 * <p>이 객체는 주로 검증 과정에서 발생한 특정 필드의 잘못된 값이나
 * 글로벌 에러(폼 전체에 대한 오류)를 클라이언트에 전달할 때 사용된다.
 * {@link ErrorResponse}의 {@code details}에 포함되어 추가 정보를 제공할 수 있다.</p>
 *
 * <p>필드는 다음과 같은 의미를 가진다:</p>
 * <ul>
 *     <li>{@code field}: 오류가 발생한 필드명 (전역 오류의 경우 {@code "global"})</li>
 *     <li>{@code value}: 사용자가 입력한 잘못된 값</li>
 *     <li>{@code reason}: 오류가 발생한 이유 또는 검증 실패 메시지</li>
 * </ul>
 *
 * <p>일반적인 사용 예:</p>
 *
 * <pre>{@code
 * // 필드 오류 생성
 * FieldError error = FieldError.of("email", "abc@", "이메일 형식이 올바르지 않습니다.");
 *
 * // 전역 오류 생성
 * FieldError global = FieldError.global("요청 데이터가 올바르지 않습니다.");
 *
 * // ErrorResponse에 포함
 * return ErrorResponse.of("INVALID_INPUT", "입력값이 유효하지 않습니다.", List.of(error));
 * }</pre>
 *
 * @author 김지원
 * @since 0.2.0
 */
@Getter
public class FieldError {

    /**
     * 오류가 발생한 필드 이름.
     * <p>전역 오류(global error)의 경우 {@code "global"}로 설정된다.</p>
     */
    private final String field;

    /**
     * 사용자가 입력한 잘못된 값.
     * <p>전역 오류의 경우 {@code null}</p>
     */
    private final Object value;

    /**
     * 오류가 발생한 이유 또는 검증 실패 메시지.
     */
    private final String reason;

    private FieldError(String field, Object value, String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    /**
     * 지정된 필드에 대한 오류를 생성한다.
     *
     * @param field  오류가 발생한 필드명
     * @param value  사용자가 입력한 값
     * @param reason 오류 발생 이유 또는 메시지
     * @return 생성된 {@link FieldError}
     */
    public static FieldError of(String field, Object value, String reason) {
        return new FieldError(field, value, reason);
    }

    /**
     * 전역 오류(특정 필드에 속하지 않는 오류)를 생성한다.
     *
     * @param reason 오류 사유
     * @return 전역 오류를 나타내는 {@link FieldError}
     */
    public static FieldError global(String reason) {
        return new FieldError("global", null, reason);
    }
}
