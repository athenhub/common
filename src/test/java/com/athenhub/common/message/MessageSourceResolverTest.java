package com.athenhub.common.message;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageSourceResolverTest {

    static MessageSourceResolver resolver;

    @BeforeAll
    static void setUp() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasenames("messages");
        ms.setDefaultEncoding("UTF-8");
        resolver = new MessageSourceResolver(ms);
    }

    @Test
    @DisplayName("메시지 코드가 존재하면 실제 메시지로 변환한다")
    void shouldReturnResolvedMessageWhenCodeExists() {
        // when
        String message = resolver.resolve("test.hello");

        // then
        assertThat(message).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("메시지 포맷 인자가 존재하면 적용하여 반환한다")
    void shouldApplyArgumentsToMessage() {
        // when
        String message = resolver.resolve("test.welcome", "지원");

        // then
        assertThat(message).isEqualTo("지원님 환영합니다");
    }

    @Test
    @DisplayName("메시지 코드가 존재하지 않으면 원본 코드를 반환한다")
    void shouldReturnCodeWhenMessageNotFound() {
        // when
        String message = resolver.resolve("unknown.code");

        // then
        assertThat(message).isEqualTo("unknown.code");
    }
}
