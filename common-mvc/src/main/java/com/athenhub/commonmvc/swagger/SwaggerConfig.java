package com.athenhub.commonmvc.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Member 서비스의 Swagger / OpenAPI 설정을 담당하는 구성 클래스.
 *
 * <p>springdoc-openapi를 기반으로 다음과 같은 역할을 수행한다.
 *
 * <ul>
 *   <li>기본 OpenAPI 메타 정보(title, description 등) 설정
 *   <li>JWT Bearer 인증 스키마를 정의하고, 모든 API에 기본 보안 요구사항으로 추가
 *   <li>Gateway를 통해 접근하는 실제 경로(/v1/members/**)가 문서에 반영되도록 경로를 변환
 * </ul>
 */
@Configuration
public class SwaggerConfig {
  /** Gateway에서 Member 서비스로 라우팅될 때 사용되는 공통 경로(prefix). */
  private static final String pathPrefix = "/v1/products";

  /**
   * 기본 OpenAPI 정보와 JWT 보안 설정을 구성한다.
   *
   * <p>다음과 같은 설정을 수행한다.
   *
   * <ul>
   *   <li>문서 상의 Server URL을 외부에서 주입받은 base URL로 설정
   *   <li>HTTP Bearer(JWT) 타입의 SecurityScheme를 등록
   *   <li>등록된 Bearer 스키마를 전역 SecurityRequirement로 추가하여 기본 인증 요구사항으로 사용
   *   <li>문서 제목 및 설명 등 기본 메타 정보 설정
   * </ul>
   *
   * @param url 게이트웨이 또는 해당 서비스에 접근하기 위한 base URL (예: {@code
   *     https://api.athenhub.xyz/member-service})
   * @return 구성된 {@link OpenAPI} 스펙 객체
   */
  @Bean
  public OpenAPI openApi(@Value("${openapi.service.url}") String url) {
    return new OpenAPI()
        .servers(List.of(new Server().url(url)))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Bearer",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("Bearer"))
        .info(new Info().title("Athen Hub").description("MEMBER API"));
  }

  /**
   * Member 서비스의 실제 엔드포인트 경로 앞에 Gateway prefix({@code /v1/members})를 추가하여, 클라이언트 관점에서 보이는 최종 경로가
   * OpenAPI 문서에 반영되도록 경로를 변환하는 커스터마이저를 등록한다.
   *
   * <p>동작 방식은 다음과 같다.
   *
   * <ul>
   *   <li>현재 등록된 모든 path를 복사하여 {@code /v1/members} prefix가 붙은 경로로 다시 추가
   *   <li>원본 경로(서비스 내부 경로)는 제거하고, Gateway를 통한 외부 공개 경로만 문서에 남긴다.
   *   <li>이미 prefix가 붙은 경로는 중복 추가하지 않는다.
   * </ul>
   *
   * <p>예시:
   *
   * <ul>
   *   <li>실제 컨트롤러 경로: {@code /members}
   *   <li>문서에 노출되는 경로: {@code /v1/members/members}
   * </ul>
   *
   * @return OpenAPI의 paths를 변환하는 {@link OpenApiCustomizer} 구현체
   */
  @Bean
  public OpenApiCustomizer addPrefixToPaths() {
    return openApi -> {
      Paths paths = openApi.getPaths();
      if (paths == null) {
        return;
      }

      // 기존 paths를 복사하여 prefix를 붙인 새로운 경로를 추가
      Map<String, PathItem> original = new LinkedHashMap<>(paths);
      for (String path : original.keySet().toArray(new String[0])) {
        String prefixed = pathPrefix + path;
        if (!paths.containsKey(prefixed)) {
          PathItem item = original.get(path);
          paths.addPathItem(prefixed, item);
        }
      }

      // prefix가 없는 실제 내부 경로는 제거하고,
      // Gateway를 통해 접근하는 외부 공개 경로만 문서에 남긴다.
      for (String path : original.keySet()) {
        if (!path.startsWith(pathPrefix)) {
          paths.remove(path);
        }
      }
    };
  }
}
