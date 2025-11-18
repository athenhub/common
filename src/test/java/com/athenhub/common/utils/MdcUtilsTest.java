package com.athenhub.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcUtilsTest {
  @AfterEach
  void tearDown() {
    MDC.clear(); // 각 테스트 후 MDC 초기화
  }

  @Test
  @DisplayName("MDC에 값을 저장하고 조회할 수 있어야 한다")
  void putAndGet_shouldWork() {
    MdcUtils.put("key", "value");

    assertThat(MdcUtils.get("key")).isEqualTo("value");
  }

  @Test
  @DisplayName("null 키나 null 값은 저장되지 않아야 한다")
  void put_nullKeyOrValue_shouldNotStore() {
    MdcUtils.put(null, "value");
    MdcUtils.put("key", null);

    assertThat(MdcUtils.get(null)).isNull();
    assertThat(MdcUtils.get("key")).isNull();
  }

  @Test
  @DisplayName("MDC에서 값을 제거할 수 있어야 한다")
  void remove_shouldWork() {
    MdcUtils.put("key", "value");

    MdcUtils.remove("key");

    assertThat(MdcUtils.get("key")).isNull();
  }

  @Test
  @DisplayName("MDC 전체를 클리어할 수 있어야 한다")
  void clear_shouldWork() {
    MdcUtils.put("key1", "value1");
    MdcUtils.put("key2", "value2");

    MdcUtils.clear();

    assertThat(MdcUtils.get("key1")).isNull();
    assertThat(MdcUtils.get("key2")).isNull();
  }

  @Test
  @DisplayName("요청 ID를 저장하고 조회할 수 있어야 한다")
  void setAndGetRequestId_shouldWork() {
    String requestId = UUID.randomUUID().toString();
    MdcUtils.setRequestId(requestId);

    assertThat(MdcUtils.getRequestId()).isEqualTo(requestId);
    assertThat(MdcUtils.getRequestUuid()).isEqualTo(UUID.fromString(requestId));
  }

  @Test
  @DisplayName("잘못된 UUID 문자열이면 getRequestUuid는 null을 반환해야 한다")
  void getRequestUuid_invalidFormat_shouldReturnNull() {
    MdcUtils.setRequestId("not-a-uuid");

    assertThatThrownBy(MdcUtils::getRequestUuid).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("인증 계정 정보를 저장하고 조회할 수 있어야 한다")
  void setAndGetRequestUsername_shouldWork() {
    MdcUtils.setRequestUsername("user1");

    assertThat(MdcUtils.getRequestUsername()).isEqualTo("user1");
  }
}
