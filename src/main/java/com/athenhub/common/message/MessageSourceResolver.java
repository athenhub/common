package com.athenhub.common.message;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

/**
 * {@link MessageResolver}의 기본 구현체로, 스프링의 {@link MessageSource}를 사용하여 메시지 코드를 실제 문자열로 변환한다.
 *
 * <p>이 구현체는 생성자를 통해 {@link MessageSource}를 주입받으며, 국제화(i18n) 메시지 파일(예: {@code messages.properties})을
 * 기반으로 메시지를 조회한다. 조회 실패 시 예외를 던지지 않고 메시지 코드를 그대로 반환하여 안전한 예외 메시지 처리가 가능하도록 한다.
 *
 * <p>기본 조회 Locale은 {@link Locale#KOREAN}이며, 필요 시 사용자 환경에 맞게 별도 Resolver 또는 LocaleContext 기반 구조로 확장할
 * 수 있다.
 *
 * <p>일반적인 사용 예:
 *
 * <pre>{@code
 * @Bean
 * public MessageResolver messageResolver(MessageSource messageSource) {
 *     return new MessageSourceResolver(messageSource);
 * }
 *
 * // 예외 핸들러 내부
 * String message = messageResolver.resolve("USER_NOT_FOUND", userId);
 * }</pre>
 *
 * @author 김지원
 * @since 0.2.0
 */
@RequiredArgsConstructor
public class MessageSourceResolver implements MessageResolver {

  /** 메시지 조회 시 기본적으로 사용할 Locale. */
  private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

  /**
   * 스프링 메시지 소스.
   *
   * <p>messages.properties와 같은 메시지 파일을 조회하는 데 사용된다.
   */
  private final MessageSource messageSource;

  /**
   * 메시지 코드를 조회하여 포맷팅된 문자열을 반환한다.
   *
   * <p>조회 실패 시 예외를 발생시키지 않고 원본 메시지 코드를 그대로 반환한다.
   *
   * @param code 메시지 코드
   * @param args 메시지 포맷 인자
   * @return 조회된 메시지 또는 원본 코드
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
