package com.athenhub.common.handler.mvc.utils;

import com.athenhub.common.error.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * 다양한 검증 예외를 {@link FieldError} 리스트로 변환하는 공통 파서.
 *
 * <p>MethodArgumentNotValidException(요청 바디 검증 실패)과
 * HandlerMethodValidationException(메서드 파라미터 검증 실패)을
 * 동일한 {@code FieldError} 리스트로 변환한다.</p>
 *
 * @author 김지원
 * @since 0.2.1
 */
public class ValidationErrorParser {

    /**
     * Bean Validation 오류 목록을 FieldError 리스트로 변환한다.
     *
     * @param e MethodArgumentNotValidException
     * @return 변환된 FieldError 리스트
     */
    public static List<FieldError> from(MethodArgumentNotValidException e) {
        return Stream.concat(
                e.getBindingResult().getFieldErrors().stream()
                        .map(it -> FieldError.of(it.getField(), it.getRejectedValue(), it.getDefaultMessage())),
                e.getBindingResult().getGlobalErrors().stream()
                        .map(it -> FieldError.global(it.getDefaultMessage()))
        ).toList();
    }

    /**
     * HandlerMethodValidationException 내부의 파라미터 검증 오류를
     * FieldError 리스트로 변환한다.
     *
     * @param e HandlerMethodValidationException
     * @return 변환된 FieldError 리스트
     */
    public static List<FieldError> from(HandlerMethodValidationException e) {
        return e.getParameterValidationResults().stream()
                .map(result -> FieldError.of(
                        result.getMethodParameter().getParameterName(),
                        result.getArgument(),
                        result.getResolvableErrors().get(0).getDefaultMessage()
                ))
                .toList();
    }
}
