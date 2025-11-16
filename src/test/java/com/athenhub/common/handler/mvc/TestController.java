package com.athenhub.common.handler.mvc;

import com.athenhub.common.error.AbstractApplicationException;
import com.athenhub.common.error.ErrorCode;
import com.athenhub.common.error.GlobalErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/app-ex")
    public void throwAppException() {
        throw new TestApplicationException(GlobalErrorCode.NOT_FOUND);
    }

    @GetMapping("/app-ex-custom")
    public void throwAppExceptionWithCustomExceptionMessage() {
        throw new TestApplicationException(GlobalErrorCode.NOT_FOUND, "MessageResolver를 사용하지 않은 커스텀 메세지");
    }

    @PostMapping("/invalid-request-body")
    public void throwInvalidRequestBodyException(@Valid @RequestBody PersonRequest request) {

    }

    @GetMapping("/invalid-path-variable/{id}")
    public void throwInvalidVariableException(@Valid @NotBlank @PathVariable(name = "id") String id) {

    }

    @GetMapping("/invalid-request-parm")
    public void throwInvalidParamException(@Valid @NotBlank @RequestParam(name = "id") String id) {

    }

    @GetMapping("/ex")
    public void throwException() {
        throw new RuntimeException("boom");
    }

    @PostMapping("/invalid-method")
    public String postOnly() {
        return "ok";
    }

    // 2) JSON 파싱 오류 유도
    @PostMapping("/invalid-json")
    public String jsonParseException(@RequestBody PersonRequest request) {
        return "ok";
    }

    // 3) TypeMismatch 유도 (id 는 Long인데 문자열 전달)
    @GetMapping("/mismatch/{id}")
    public String typeMismatchException(@PathVariable Long id) {
        return "ok";
    }

    static class TestApplicationException extends AbstractApplicationException {

        public TestApplicationException(ErrorCode errorCode, Object... errorArgs) {
            super(errorCode, errorArgs);
        }

        public TestApplicationException(ErrorCode errorCode, String message, Object... errorArgs) {
            super(errorCode, message, errorArgs);
        }
    }

    public record PersonRequest(
            @NotBlank
            String name,
            @Min(1)
            int age
    ) {
    }
}


