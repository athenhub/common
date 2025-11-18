package com.athenhub.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

/**
 * JSON 변환을 위한 유틸리티 클래스.
 *
 * <pre>
 * - 싱글톤 Gson 인스턴스를 제공
 * - LocalDateTime 직렬화/역직렬화 어댑터 등록
 * </pre>
 *
 * @author 김형섭
 * @since 0.3.0
 */
public class GsonUtils {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
          .create();

  /** 인스턴스 생성 방지를 위한 private 생성자. */
  private GsonUtils() {}

  /**
   * 주어진 객체를 JSON 문자열로 변환한다.
   *
   * @param object 변환할 객체
   * @return JSON 문자열
   */
  public static String toJson(Object object) {
    return GSON.toJson(object);
  }

  /**
   * 주어진 JSON 문자열을 지정된 클래스 타입의 객체로 변환한다.
   *
   * @param json 변환할 JSON 문자열
   * @param clazz 대상 클래스 타입
   * @param <T> 반환할 객체 타입
   * @return 변환된 객체
   */
  public static <T> T fromJson(String json, Class<T> clazz) {
    return GSON.fromJson(json, clazz);
  }
}
