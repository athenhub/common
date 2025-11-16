package com.athenhub.common.handler.mvc.utils;

import com.athenhub.common.error.FieldError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@ExtendWith(MockitoExtension.class)
public class ValidationErrorParserTest {

    @Nested
    @DisplayName("from(MethodArgumentNotValidException) 테스트")
    class MethodArgumentNotValidExceptionTest {

        @Mock
        BindingResult bindingResult;
        @Mock
        MethodArgumentNotValidException ex;

        @Test
        @DisplayName("필드 오류만 존재할 때 FieldError 리스트 생성")
        void fieldErrorsOnly() {
            // given
            org.springframework.validation.FieldError f1 = new org.springframework.validation.FieldError("obj", "username", "jiwon", false, null, null, "invalid username");
            org.springframework.validation.FieldError f2 = new org.springframework.validation.FieldError("obj", "age", -1, false, null, null, "age must be positive");

            given(bindingResult.getFieldErrors()).willReturn(List.of(f1, f2));
            given(bindingResult.getGlobalErrors()).willReturn(List.of());
            given(ex.getBindingResult()).willReturn(bindingResult);

            // when
            List<FieldError> result = ValidationErrorParser.from(ex);

            // then
            assertThat(result)
                    .hasSize(2)
                    .satisfiesExactly(
                            e -> {
                                assertThat(e.getField()).isEqualTo("username");
                                assertThat(e.getValue()).isEqualTo("jiwon");
                                assertThat(e.getReason()).isEqualTo("invalid username");
                            },
                            e -> {
                                assertThat(e.getField()).isEqualTo("age");
                                assertThat(e.getValue()).isEqualTo(-1);
                                assertThat(e.getReason()).isEqualTo("age must be positive");
                            });
        }

        @Test
        @DisplayName("전역(Global) 오류만 존재할 때 FieldError.global 반환")
        void globalErrorsOnly() {
            // given
            ObjectError g1 = new ObjectError("obj", "global issue");

            given(bindingResult.getFieldErrors()).willReturn(List.of());
            given(bindingResult.getGlobalErrors()).willReturn(List.of(g1));
            given(ex.getBindingResult()).willReturn(bindingResult);

            // when
            List<FieldError> result = ValidationErrorParser.from(ex);

            // then
            assertThat(result).hasSize(1)
                    .first()
                    .satisfies(
                            error -> {
                                assertThat(error.getField()).isEqualTo("global");
                                assertThat(error.getValue()).isNull();
                                assertThat(error.getReason()).isEqualTo("global issue");
                            }
                    );
        }

        @Test
        @DisplayName("필드 오류 + 글로벌 오류가 함께 있을 때 모든 오류를 반환")
        void fieldAndGlobalErrors() {
            // given
            org.springframework.validation.FieldError f1 = new org.springframework.validation.FieldError("obj", "email", "wrong", false, null, null, "invalid email");
            ObjectError g1 = new ObjectError("obj", "global fail");

            given(bindingResult.getFieldErrors()).willReturn(List.of(f1));
            given(bindingResult.getGlobalErrors()).willReturn(List.of(g1));
            given(ex.getBindingResult()).willReturn(bindingResult);

            // when
            List<FieldError> result = ValidationErrorParser.from(ex);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("email");
            assertThat(result.get(1).getField()).isEqualTo("global");
        }
    }

    @Nested
    @DisplayName("from(HandlerMethodValidationException) 테스트")
    class HandlerMethodValidationExceptionTest {

        @Mock
        HandlerMethodValidationException ex;

        @Test
        @DisplayName("단일 파라미터 에러를 FieldError 로 변환")
        void singleParameterError() {
            // given
            ParameterValidationResult mockResult = mock(ParameterValidationResult.class);
            MethodParameter param = mock(MethodParameter.class);
            MessageSourceResolvable res = mock(MessageSourceResolvable.class);

            given(param.getParameterName()).willReturn("name");
            given(mockResult.getMethodParameter()).willReturn(param);
            given(mockResult.getArgument()).willReturn("");
            given(res.getDefaultMessage()).willReturn("must not be blank");
            given(mockResult.getResolvableErrors()).willReturn(List.of(res));

            given(ex.getParameterValidationResults()).willReturn(List.of(mockResult));

            // when
            List<FieldError> result = ValidationErrorParser.from(ex);

            // then
            assertThat(result)
                    .hasSize(1)
                    .first()
                    .satisfies(error -> {
                        assertThat(error.getField()).isEqualTo("name");
                        assertThat(error.getValue()).isEqualTo("");
                        assertThat(error.getReason()).isEqualTo("must not be blank");
                    });
        }

        @Test
        @DisplayName("여러 ParameterValidationResult 를 모두 FieldError 로 변환")
        void multipleParameterErrors() {
            // given
            ParameterValidationResult r1 = mock(ParameterValidationResult.class);
            ParameterValidationResult r2 = mock(ParameterValidationResult.class);

            MethodParameter p1 = mock(MethodParameter.class);
            MethodParameter p2 = mock(MethodParameter.class);

            MessageSourceResolvable e1 = mock(MessageSourceResolvable.class);
            MessageSourceResolvable e2 = mock(MessageSourceResolvable.class);

            // 첫 번째 에러
            given(p1.getParameterName()).willReturn("id");
            given(r1.getMethodParameter()).willReturn(p1);
            given(r1.getArgument()).willReturn(-1);
            given(e1.getDefaultMessage()).willReturn("must be greater than zero");
            given(r1.getResolvableErrors()).willReturn(List.of(e1));

            // 두 번째 에러
            given(p2.getParameterName()).willReturn("email");
            given(r2.getMethodParameter()).willReturn(p2);
            given(r2.getArgument()).willReturn("wrong!");
            given(e2.getDefaultMessage()).willReturn("invalid email");
            given(r2.getResolvableErrors()).willReturn(List.of(e2));

            given(ex.getParameterValidationResults()).willReturn(List.of(r1, r2));

            // when
            List<FieldError> result = ValidationErrorParser.from(ex);

            // then
            assertThat(result)
                    .hasSize(2)
                    .satisfiesExactly(
                            e -> {
                                assertThat(e.getField()).isEqualTo("id");
                                assertThat(e.getValue()).isEqualTo(-1);
                                assertThat(e.getReason()).isEqualTo("must be greater than zero");
                            },
                            e -> {
                                assertThat(e.getField()).isEqualTo("email");
                                assertThat(e.getValue()).isEqualTo("wrong!");
                                assertThat(e.getReason()).isEqualTo("invalid email");
                            });
        }
    }
}
