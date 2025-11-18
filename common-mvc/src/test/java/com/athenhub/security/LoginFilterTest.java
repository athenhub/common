package com.athenhub.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.commonmvc.security.AuthenticatedUser;
import com.athenhub.commonmvc.security.filter.LoginFilter;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {
  private LoginFilter loginFilter;

  @BeforeEach
  void setUp() {
    loginFilter = new LoginFilter();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("필수 헤더가 없으면 인증되지 않음")
  void unauthorizedIfEmptyHeader() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    loginFilter.doFilter(request, response, new MockFilterChain());

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @DisplayName("헤더가 있으면 Authentication 저장됨")
  void authorized() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-User-Id", UUID.randomUUID().toString());
    request.addHeader("X-Username", "test");
    request.addHeader("X-User-Name", URLEncoder.encode("홍 길동", StandardCharsets.UTF_8));
    request.addHeader("X-Slack-Id", "SL123");
    request.addHeader("X-User-Roles", "ROLE_ADMIN,ROLE_USER");

    loginFilter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal()).isInstanceOf(AuthenticatedUser.class);

    AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
    assertThat(user.name()).isEqualTo("홍 길동");
    assertThat(user.slackId()).isEqualTo("SL123");
    assertThat(user.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
  }
}
