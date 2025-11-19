package com.athenhub.commonmvc.logging;

import io.micrometer.common.util.StringUtils;
import java.util.UUID;
import org.slf4j.MDC;

/**
 * MDC (Mapped Diagnostic Context) 유틸리티 클래스.
 *
 * <pre>
 * - 요청 ID 및 인증 계정 등 로그 컨텍스트를 위한 MDC 키 관리
 * - MDC에 값 저장, 조회, 제거, 전체 클리어 기능 제공
 * </pre>
 *
 * @author 김형섭
 * @since 0.3.0
 */
public class MdcUtils {

  public static final String REQUEST_ID = "requestId";
  public static final String REQUEST_USERNAME = "requestUsername";

  /** 인스턴스 생성 방지를 위한 private 생성자. */
  private MdcUtils() {}

  /**
   * MDC에 키-값 쌍을 저장한다.
   *
   * @param key MDC 키
   * @param value 저장할 값
   */
  public static void put(String key, String value) {
    if (key != null && value != null) {
      MDC.put(key, value);
    }
  }

  /**
   * MDC에서 지정된 키의 값을 조회한다.
   *
   * @param key 조회할 MDC 키
   * @return 해당 키의 값, 없거나 키가 null인 경우 null
   */
  public static String get(String key) {
    return key != null ? MDC.get(key) : null;
  }

  /**
   * MDC에서 지정된 키의 값을 제거한다.
   *
   * @param key 제거할 MDC 키
   */
  public static void remove(String key) {
    if (key != null) {
      MDC.remove(key);
    }
  }

  /** MDC의 모든 키-값 쌍을 클리어한다. */
  public static void clear() {
    MDC.clear();
  }

  /**
   * MDC에 요청 ID를 저장한다.
   *
   * @param requestId 요청 ID 문자열
   */
  public static void setRequestId(String requestId) {
    put(REQUEST_ID, requestId);
  }

  /**
   * MDC에서 요청 ID를 조회한다.
   *
   * @return 요청 ID 문자열, 없으면 null
   */
  public static String getRequestId() {
    return get(REQUEST_ID);
  }

  /**
   * MDC에서 요청 ID를 UUID 형식으로 조회한다.
   *
   * @return 요청 ID UUID, 없거나 형식이 올바르지 않으면 null
   */
  public static UUID getRequestUuid() {
    String requestId = get(REQUEST_ID);
    if (StringUtils.isBlank(requestId)) {
      return null;
    }
    return UUID.fromString(requestId);
  }

  /**
   * MDC에 인증 계정 정보를 저장한다.
   *
   * @param requestUsername 인증 계정 문자열
   */
  public static void setRequestUsername(String requestUsername) {
    put(REQUEST_USERNAME, requestUsername);
  }

  /**
   * MDC에서 인증 계정 정보를 조회한다.
   *
   * @return 인증 계정 문자열, 없으면 null
   */
  public static String getRequestUsername() {
    return get(REQUEST_USERNAME);
  }
}
