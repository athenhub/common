package com.athenhub.common.handler.mvc;

import com.athenhub.common.error.AbstractApplicationException;
import com.athenhub.common.error.ErrorCode;
import com.athenhub.common.error.GlobalErrorCode;
import com.athenhub.common.message.MessageResolver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {MvcExceptionHandler.class,
        MvcExceptionHandlerTest.TestController.class
})
@WebMvcTest
class MvcExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageResolver messageResolver;

    @Test
    @DisplayName("[GET] /test/app-ex - ApplicationException 발생 시 404 NOT_FOUND 반환")
    void getShouldReturnNotFoundWhenApplicationExceptionThrown() throws Exception {
        // given
        String code = "NOT_FOUND";
        String message = "요청하신 리소스를 찾을 수 없습니다.";
        given(messageResolver.resolve(code))
                .willReturn(message);

        // when & then
        mockMvc.perform(get("/test/app-ex"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    @DisplayName("[GET] /test/app-ex-custom - ApplicationException + 커스텀 메시지 사용 시 404 NOT_FOUND 반환")
    void getShouldReturnNotFoundWithCustomMessageWhenApplicationExceptionWithArgumentsThrown() throws Exception {
        // given
        String code = "NOT_FOUND";
        String resolvedMessage = "회원을 찾을 수 없습니다.";

        // when & then
        mockMvc.perform(get("/test/app-ex-custom"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(resolvedMessage));
    }

    @Test
    @DisplayName("[POST] /test/valid-ex - 요청 데이터 검증 실패 시 400 VALIDATION_ERROR 반환")
    void postShouldReturnValidationErrorWhenRequestIsInvalid() throws Exception {
        // given
        String code = "VALIDATION_ERROR";
        String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
        given(messageResolver.resolve(code))
                .willReturn(message);
        String request = """
                {
                    "name" : "",
                    "age" : -1
                }
                """;

        // when & then
        mockMvc.perform(post("/test/valid-ex")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("[GET] /test/ex - 알 수 없는 예외 발생 시 500 INTERNAL_SERVER_ERROR 반환")
    void getShouldReturnInternalServerErrorWhenUnknownExceptionThrown() throws Exception {
        //given
        String code = "INTERNAL_SERVER_ERROR";
        String message = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        given(messageResolver.resolve(code))
                .willReturn(message);

        // when & then
        mockMvc.perform(get("/test/ex"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/app-ex")
        public void throwAppException() {
            throw new TestApplicationException(GlobalErrorCode.NOT_FOUND);
        }

        @GetMapping("/app-ex-custom")
        public void throwAppExceptionWithCustomExceptionMessage() {
            throw new TestApplicationException(GlobalErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다.");
        }

        @PostMapping("/valid-ex")
        public void throwValidException(@Valid @RequestBody PersonRequest request) {
        }

        @GetMapping("/ex")
        public void throwException() {
            throw new RuntimeException("boom");
        }
    }

    static class TestApplicationException extends AbstractApplicationException {

        public TestApplicationException(ErrorCode errorCode, Object... errorArgs) {
            super(errorCode, errorArgs);
        }

        public TestApplicationException(ErrorCode errorCode, String message, Object... errorArgs) {
            super(errorCode, message, errorArgs);
        }
    }

    static class PersonRequest {
        @NotEmpty
        String name;
        @Min(1)
        int age;
    }

}