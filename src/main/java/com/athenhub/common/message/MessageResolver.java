package com.athenhub.common.message;

/**
 * 메시지 코드와 전달된 인자를 기반으로 실제 출력할 메시지를 생성하는 인터페이스.
 *
 * <p>주로 예외 처리 또는 응답 메시지 변환 시 사용되며,
 * 국제화(i18n) 또는 커스텀 메시지 전략을 적용하는 역할을 담당한다.</p>
 *
 * <p>예시:</p>
 * <pre>
 *     resolve("user.not_found", "홍길동")
 *     → "홍길동 사용자를 찾을 수 없습니다."
 * </pre>
 *
 *
 * @author 김지원
 * @since 0.2.0
 */
public interface MessageResolver {

    /**
     * 주어진 메시지 코드와 가변 인자를 기반으로 실제 메시지를 변환하여 반환한다.
     *
     * @param code 메시지 코드 (예: "NOT_FOUND", "user.not_found")
     * @param args 메시지 포맷팅 시 사용될 인자 목록
     * @return 변환된 실제 메시지 문자열
     */
    String resolve(String code, Object... args);
}