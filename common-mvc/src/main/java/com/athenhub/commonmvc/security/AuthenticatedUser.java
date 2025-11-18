package com.athenhub.commonmvc.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**
 * Spring Security 인증 모델을 표현하는 사용자 정보 클래스.
 *
 * <p>외부 인증 시스템 또는 API Gateway에서 전달된 사용자 정보를 기반으로 애플리케이션 내부에서 인증된 사용자로 동작할 수 있도록 구현된 {@link
 * UserDetails} 커스텀 구현체입니다.
 *
 * <p>해당 클래스는 다음과 같은 사용자 속성을 포함합니다:
 *
 * <ul>
 *   <li>{@code id} — 사용자 UUID
 *   <li>{@code username} — 로그인 ID
 *   <li>{@code name} — 사용자 이름(실명)
 *   <li>{@code slackId} — Slack 사용자 ID
 *   <li>{@code roles} — 쉼표(,)로 구분된 역할 문자열
 * </ul>
 *
 * <p>권한 정보는 {@code roles} 값을 쉼표로 분리하여 {@link SimpleGrantedAuthority}로 매핑되며, 값이 없을 경우 기본값으로 {@code
 * ROLE_USER}를 부여합니다.
 *
 * <p>비밀번호 기반 인증을 사용하지 않기 때문에 {@link #getPassword()}는 빈 문자열을 반환합니다.
 *
 * @param id ID
 * @param username 계정
 * @param name 이름
 * @param slackId 슬랙 ID
 * @param roles 권한
 * @author 김형섭
 * @since 1.0.0
 */
public record AuthenticatedUser(UUID id, String username, String name, String slackId, String roles)
    implements UserDetails {

  /**
   * {@link AuthenticatedUser} 객체를 생성하는 정적 팩토리 메서드.
   *
   * <p>직접 생성자를 노출하지 않고 명시적인 생성 의미를 전달하기 위해 사용됩니다.
   *
   * @param id 사용자 UUID
   * @param username 로그인 ID
   * @param name 사용자 이름
   * @param slackId Slack ID
   * @param roles 쉼표로 구분된 역할 목록
   * @return 생성된 {@link AuthenticatedUser} 인스턴스
   */
  public static AuthenticatedUser of(
      UUID id, String username, String name, String slackId, String roles) {
    return new AuthenticatedUser(id, username, name, slackId, roles);
  }

  /**
   * 사용자 역할 목록을 {@link GrantedAuthority} 컬렉션으로 변환한다.
   *
   * <p>{@code roles} 문자열을 쉼표로 구분하여 리스트로 변환하고, 각 값을 {@link SimpleGrantedAuthority} 객체로 감싸 반환한다.
   *
   * <p>{@code roles} 값이 비어있는 경우 기본 권한 {@code ROLE_USER}가 부여된다.
   *
   * @return 사용자 권한 목록
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<String> userRoles =
        StringUtils.hasText(roles) ? Arrays.asList(roles.split(",")) : List.of("ROLE_USER");

    return userRoles.stream().map(SimpleGrantedAuthority::new).toList();
  }

  /**
   * 비밀번호 기반 인증을 사용하지 않기 때문에 빈 문자열을 반환한다.
   *
   * @return 빈 문자열
   */
  @Override
  public String getPassword() {
    return "";
  }

  /**
   * Spring Security가 사용하는 사용자 ID(username)를 반환한다.
   *
   * @return 로그인 ID
   */
  @Override
  public String getUsername() {
    return username;
  }
}
