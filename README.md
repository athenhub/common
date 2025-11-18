# 📦 athenhub/common 라이브러리

# 💡0.3.0 버전 기능

- LoggingAspect.class
    - AOP 기반의 LoggingAspect 및 MDC 필터 추가
    - 애플리케이션 전반의 컨트롤러 진입/종료 시점을 AOP로 로깅 처리하는 Aspect 클래스
    - RestController 내의 모든 요청에 대해 HTTP 메서드, URI, 메서드명, 파라미터, 응답 결과를 로깅

e.g.

```code
2025-11-18T15:58:22.702+09:00  INFO 45009 --- [nio-8080-exec-3] com.athenhub.common.logging.LogManager   : POST /ex5 - Request ID: d5843b03-724c-498b-b797-eab2554cee55, Username: aj0123, Method: TestController.ex5 , Params: {person: Person[name=AJ, age=20]}
2025-11-18T15:58:22.702+09:00  INFO 45009 --- [nio-8080-exec-3] c.athenhub.springboottest.TestService    : Person[name=AJ, age=20]
2025-11-18T15:58:22.703+09:00  INFO 45009 --- [nio-8080-exec-3] com.athenhub.common.logging.LogManager   : POST /ex5 - Request ID: d5843b03-724c-498b-b797-eab2554cee55, Username: aj0123, Method: TestController.ex5, Return: "ok"
```

- GsonUtils.class
    - JSON 변환을 위한 유틸리티 클래스
    - 싱글톤 Gson 인스턴스를 제공
    - LocalDateTime 직렬화/역직렬화 어댑터 등록

# 💡0.2.1 버전 기능

## GlobalErrorHandler

- MvcExceptionHandler
    - Spring MVC 환경에서 발생하는 예외를 공통 형태로 응답
    - Validation, 비즈니스 예외 등 다양한 예외를 구조화된 JSON 형태로 반환
    - 사용자가 직접 @RestControllerAdvice 또는 MvcExceptionHandler 빈을 등록하면 자동 생성되지 않음
    - `athenhub.exception.mvc.enabled=true` 로 on/off 가능(default=true)
    - `AbstractApplicationException` 을 상속 받아 사용자 예외 정의 가능

- MessageResolver (MessageSourceResolver)
    - MvcExceptionHandler 에서 code 변환시 MessageResolver 를 사용
    - MessageSource 기반으로 메시지 코드 → 사람이 읽을 수 있는 메시지 변환
    - `resources/messages.properties` 에 변환하고 싶은 메세지 추가 가능
        ```properties
        user.not.found=회원을 찾을 수 없습니다.
        ```
    - 현재는 Locale은 한글만 적용, 추후 국제화 고려

# ⚙️ athenhub/common 라이브러리 설정 가이드

본 문서는 `athenhub/common` 라이브러리를 Gradle 기반 프로젝트에서 설정 방법을 정리한 가이드입니다.  
GitHub Packages 저장소를 활용하므로, 필요한 GitHub Token 발급 및 gradle.properties 설정 방법도 함께 안내합니다.

---

## 🧪 1. Maven 저장소 추가

프로젝트의 `build.gradle` 또는 `build.gradle.kts`에 아래 저장소(repository)를 추가하세요.

```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitHubPublicPackages"
        url = uri("https://maven.pkg.github.com/athenhub/common")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

> 💡 GitHub Packages는 인증 정보가 필요하므로 credentials 설정이 반드시 필요합니다.

## 📚 2. 의존성 추가

`build.gradle` 에 다음과 같이 라이브러리 의존성을 등록하세요.

```groovy
dependencies {
    implementation 'com.athenhub:common:{version}'
}
```

> 최신 버전은 아래 GitHub Packages 페이지에서 확인할 수 있습니다. <br>
> 👉 https://github.com/athenhub/common/packages

## 🔐 3. gradle.properties 설정

프로젝트 루트에 `gradle.properties` 파일을 생성하고, GitHub 인증 정보를 입력합니다.

```properties
gpr.user=깃허브ID
gpr.key=깃허브TOKEN
```

🔑 GitHub Token 발급 팁

- GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
- read:packages 권한만 있으면 충분합니다.

## ✅ 마무리

위의 과정을 모두 완료하면 athenhub/common 라이브러리를 정상적으로 가져와 프로젝트에서 사용할 수 있습니다.