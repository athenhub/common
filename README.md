# 📦 athenhub/common 라이브러리 설정 가이드

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
