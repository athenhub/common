package com.athenhub.common.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 *
 * Spring {@link MessageSource}를 사용해 메시지 코드를 실제 문자열로 변환하는 기본 구현체.
 * 조회 실패 시 예외를 던지지 않고 메시지 코드를 그대로 반환한다.
 *
 * @author 김지원
 * @since 0.2.0
 */
@RequiredArgsConstructor
public class MessageSourceResolver implements MessageResolver {

    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;
    private final MessageSource messageSource;

    /**
     * 메시지 코드를 조회하여 문자열로 반환한다.
     * 조회 실패 시 메시지 코드를 그대로 반환한다.
     *
     * @param code 메시지 코드
     * @param args 메시지 포맷 인자
     * @return 조회된 메시지 또는 원본 코드
     *
     */
    @Override
    public String resolve(String code, Object... args) {
        try {
            return messageSource.getMessage(code, args, DEFAULT_LOCALE);
        } catch (Exception e) {
            return code;
        }
    }
}
