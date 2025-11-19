package com.athenhub.commonutils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson에서 {@link LocalDateTime}을 JSON 문자열로 직렬화 및 역직렬화하기 위한 어댑터 클래스.
 *
 * <p>이 어댑터는 {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} 형식을 사용하여 LocalDateTime 객체를 문자열로 변환하며,
 * JSON 문자열을 다시 LocalDateTime으로 읽을 수 있도록 지원합니다.
 *
 * <p>사용 예시:
 *
 * <pre>
 * Gson gson = new GsonBuilder()
 *     .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
 *     .create();
 * String json = gson.toJson(LocalDateTime.now());
 * LocalDateTime dateTime = gson.fromJson(json, LocalDateTime.class);
 * </pre>
 *
 * @author 김형섭
 * @since 0.3.0
 */
public class GsonLocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Override
  public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
    if (localDateTime == null) {
      jsonWriter.nullValue();
    } else {
      jsonWriter.value(localDateTime.format(formatter));
    }
  }

  @Override
  public LocalDateTime read(JsonReader jsonReader) throws IOException {
    return LocalDateTime.parse(jsonReader.nextString(), formatter);
  }
}
