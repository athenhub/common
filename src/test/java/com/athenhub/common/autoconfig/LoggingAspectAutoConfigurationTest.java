package com.athenhub.common.autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.common.logging.LoggingAspect;
import com.athenhub.common.logging.filter.MdcFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/** LoggingAspectAutoConfiguration Test. */
class LoggingAspectAutoConfigurationTest {

  private WebApplicationContextRunner contextRunner;

  @BeforeEach
  void setup() {
    contextRunner =
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LoggingAspectAutoConfiguration.class));
  }

  @Test
  @DisplayName("기본 조건 충족 시 LoggingAspect & MdcFilter & FilterRegistrationBean 자동 등록된다")
  void defaultAutoConfigurationWorks() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(LoggingAspect.class);
          assertThat(context).hasSingleBean(MdcFilter.class);
          assertThat(context).hasSingleBean(FilterRegistrationBean.class);
        });
  }

  @Test
  @DisplayName("enabled=false 이면 LoggingAspect & MdcFilter & FilterRegistrationBean 모두 등록되지 않는다")
  void disabledProperty() {
    contextRunner
        .withPropertyValues("athenhub.logging.mvc.enabled=false")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(LoggingAspect.class);
              assertThat(context).doesNotHaveBean(MdcFilter.class);
              assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
            });
  }
}
