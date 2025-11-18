package com.athenhub.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class AuthenticatedUserTest {

  @Test
  @DisplayName("roles가 있으면 Authority 정상생성")
  void authorities() {
    AuthenticatedUser user =
        AuthenticatedUser.of(UUID.randomUUID(), "test", "홍길동", "SLACK", "ROLE_ADMIN,ROLE_USER");

    assertThat(user.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
  }

  @Test
  @DisplayName("roles가_null이면_DEFAULT_ROLE_USER_부여")
  void authoritiesWithNull() {
    AuthenticatedUser user = AuthenticatedUser.of(UUID.randomUUID(), "test", "홍길동", "SLACK", null);

    assertThat(user.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_USER");
  }

  @Test
  @DisplayName("password는_항상_빈문자열")
  void getPassword() {
    AuthenticatedUser user = AuthenticatedUser.of(UUID.randomUUID(), "test", "홍길동", "SLACK", null);

    assertThat(user.getPassword()).isEmpty();
  }

  @Test
  void of() {
    UUID id = UUID.randomUUID();
    AuthenticatedUser user = AuthenticatedUser.of(id, "test", "홍길동", "SLACK", "ROLE_USER");

    assertThat(user.id()).isEqualTo(id);
    assertThat(user.getUsername()).isEqualTo("test");
    assertThat(user.name()).isEqualTo("홍길동");
    assertThat(user.slackId()).isEqualTo("SLACK");
  }
}
