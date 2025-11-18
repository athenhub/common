package com.athenhub.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GsonLocalDateTimeAdapterTest {
  private final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
          .create();

  @Test
  @DisplayName("LocalDateTime을 JSON 문자열로 직렬화할 수 있어야 한다")
  void shouldSerializeLocalDateTime() {
    LocalDateTime now = LocalDateTime.of(2025, 11, 17, 14, 0, 0);

    String json = gson.toJson(now);

    assertThat(json).isEqualTo("\"2025-11-17T14:00:00\"");
  }

  @Test
  @DisplayName("JSON 문자열을 LocalDateTime으로 역직렬화할 수 있어야 한다")
  void shouldDeserializeLocalDateTime() {
    String json = "\"2025-11-17T14:00:00\"";

    LocalDateTime result = gson.fromJson(json, LocalDateTime.class);

    assertThat(result).isEqualTo(LocalDateTime.of(2025, 11, 17, 14, 0, 0));
  }

  @Test
  @DisplayName("null 값을 직렬화하면 JSON null로 변환된다")
  void shouldSerializeNull() {
    LocalDateTime value = null;

    String json = gson.toJson(value);

    assertThat(json).isEqualTo("null");
  }

  @Test
  @DisplayName("잘못된 JSON 문자열 역직렬화 시 JsonSyntaxException 발생")
  void shouldThrowExceptionForInvalidJson() {
    String invalidJson = "\"invalid-datetime\"";

    assertThatThrownBy(() -> gson.fromJson(invalidJson, LocalDateTime.class))
        .isInstanceOf(DateTimeParseException.class);
  }
}
