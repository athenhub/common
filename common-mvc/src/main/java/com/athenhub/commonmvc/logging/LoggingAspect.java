package com.athenhub.commonmvc.logging;

import com.athenhub.commonmvc.utils.GsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

/**
 * 애플리케이션 전반의 컨트롤러 진입/종료 시점을 AOP로 로깅 처리하는 Aspect 클래스.
 *
 * <pre>
 * - RestController 내의 모든 요청에 대해 HTTP 메서드, URI, 메서드명, 파라미터, 응답 결과를 로깅
 * </pre>
 *
 * @author 김형섭
 * @since 0.3.0
 */
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

  private static final String NOT_APPLICABLE = "N/A";

  private final LogManager logManager;

  /**
   * RestController 범위 내의 모든 메서드 실행 시점에 대해 진입과 종료를 로깅한다.
   *
   * @param pjp 호출 대상 JoinPoint
   * @return 실제 메서드 실행 결과
   * @throws Throwable 내부 메서드 예외 발생 시 전달
   */
  @Around("within(@org.springframework.web.bind.annotation.RestController *)")
  public Object logController(ProceedingJoinPoint pjp) throws Throwable {
    HttpServletRequest request = getCurrentHttpRequest();
    String httpMethod = request == null ? NOT_APPLICABLE : request.getMethod();
    String requestUri =
        request == null ? NOT_APPLICABLE : extractPath(request.getRequestURL().toString());
    String methodInfo = extractMethodInfo(pjp);
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    String logMessage = buildLogMessage(signature, pjp.getArgs());

    logManager.logControllerEntry(httpMethod, requestUri, methodInfo, logMessage);

    Object result = pjp.proceed();
    String resultJson;
    try {
      resultJson = GsonUtils.toJson(result);
    } catch (Exception e) {
      resultJson = result.getClass().getName();
    }

    logManager.logControllerExit(httpMethod, requestUri, methodInfo, resultJson);

    return result;
  }

  /**
   * 현재 HTTP 요청 객체를 조회한다.
   *
   * @return HttpServletRequest 현재 요청, 없으면 null
   */
  private HttpServletRequest getCurrentHttpRequest() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }

  private String extractSimpleClassName(String fullClassName) {
    int lastDotIndex = fullClassName.lastIndexOf(".");
    return lastDotIndex != -1 ? fullClassName.substring(lastDotIndex + 1) : fullClassName;
  }

  /**
   * 전체 URL 문자열에서 경로(path)를 추출한다.
   *
   * @param fullUrl 전체 URL
   * @return URL 경로, 추출 실패 시 원본 문자열
   */
  private String extractPath(String fullUrl) {
    try {
      URI uri = new URI(fullUrl);
      return uri.getPath();
    } catch (Exception e) {
      log.warn("Failed to extract path from URL: {}", fullUrl, e);
      return fullUrl;
    }
  }

  /**
   * ProceedingJoinPoint로부터 클래스명과 메서드명을 결합한 식별 문자열을 생성한다.
   *
   * @param joinPoint 호출 대상 JoinPoint
   * @return ClassName.methodName 형식의 메서드 정보
   */
  private String extractMethodInfo(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    return extractSimpleClassName(signature.getDeclaringTypeName()) + "." + signature.getName();
  }

  /**
   * 메서드 시그니처와 인자를 기반으로 파라미터 로깅용 문자열을 생성한다.
   *
   * @param signature 메서드 시그니처
   * @param args 메서드 호출 인자 배열
   * @return ", Params: {name1: value1, ...}" 형식의 파라미터 정보 (인자가 없으면 빈 문자열)
   */
  private String buildLogMessage(MethodSignature signature, Object[] args) {
    String[] parameterNames = signature.getParameterNames();
    StringBuilder logMessage = new StringBuilder();

    if (parameterNames.length > 0) {
      logMessage.append(", Params: {");
      for (int i = 0; i < parameterNames.length; i++) {
        logMessage.append(parameterNames[i]).append(": ").append(args[i]);
        if (i < parameterNames.length - 1) {
          logMessage.append(", ");
        }
      }
      logMessage.append("}");
    }

    return logMessage.toString();
  }
}
