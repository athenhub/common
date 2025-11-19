package com.athenhub.commonmvc.autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.commoncore.message.MessageResolver;
import com.athenhub.commonmvc.handler.MvcExceptionHandler;
import com.athenhub.commonmvc.message.MessageSourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** MvcExceptionHandlerAutoConfiguration Test. */
public class MvcExceptionHandlerAutoConfigurationTest {

  private WebApplicationContextRunner contextRunner;

  @BeforeEach
  void setup() {
    contextRunner =
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MvcExceptionHandlerAutoConfiguration.class));
  }

  @Test
  @DisplayName("기본 조건 충족 시 MvcExceptionHandler & MessageResolver 자동 등록된다")
  void defaultAutoConfigurationWorks() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(MvcExceptionHandler.class);
          assertThat(context).hasSingleBean(MessageResolver.class);
          assertThat(context.getBean(MessageResolver.class))
              .isInstanceOf(MessageSourceResolver.class);
        });
  }

  @Test
  @DisplayName("enabled=false 이면 MvcExceptionHandler & MessageResolver 모두 등록되지 않는다")
  void disabledProperty() {
    contextRunner
        .withPropertyValues("athenhub.exception.mvc.enabled=false")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(MvcExceptionHandler.class);
              assertThat(context).doesNotHaveBean(MessageResolver.class);
            });
  }

  @Test
  @DisplayName("@RestControllerAdvice 가 존재하면 자동 등록되지 않는다")
  void restControllerAdviceExists() {
    contextRunner
        .withUserConfiguration(TestRestControllerAdvice.class)
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(MvcExceptionHandler.class);
              assertThat(context).doesNotHaveBean(MessageResolver.class);
            });
  }

  @Test
  @DisplayName("사용자가 직접 MvcExceptionHandler 빈을 정의하면 자동 등록되지 않는다")
  void customMvcExceptionHandlerBeanExists() {
    contextRunner
        .withUserConfiguration(CustomHandlerConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(MvcExceptionHandler.class);
              assertThat(context.getBean(MvcExceptionHandler.class))
                  .isInstanceOf(CustomHandler.class);
            });
  }

  @Test
  @DisplayName("사용자가 MessageResolver를 직접 정의하면 기본 MessageSourceResolver는 등록되지 않는다")
  void customMessageResolverBeanExists() {
    contextRunner
        .withUserConfiguration(CustomMessageResolverConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(MessageResolver.class);
              assertThat(context.getBean(MessageResolver.class))
                  .isInstanceOf(CustomMessageResolver.class);
              assertThat(context).hasSingleBean(MvcExceptionHandler.class);
            });
  }

  @RestControllerAdvice
  static class TestRestControllerAdvice {}

  @Configuration
  static class CustomHandlerConfig {
    @Bean
    public MvcExceptionHandler mvcExceptionHandler() {
      return new CustomHandler();
    }
  }

  @Configuration
  static class CustomMessageResolverConfig {

    @Bean
    public MessageResolver messageResolver() {
      return new CustomMessageResolver();
    }
  }

  static class CustomHandler extends MvcExceptionHandler {
    public CustomHandler() {
      super(null);
    }
  }

  static class CustomMessageResolver implements MessageResolver {

    @Override
    public String resolve(String code, Object... args) {
      return "custom";
    }
  }
}
