package com.athenhub.common.handler.mvc;

import com.athenhub.common.error.AbstractApplicationException;
import com.athenhub.common.error.ErrorCode;
import com.athenhub.common.error.GlobalErrorCode;
import com.athenhub.common.message.MessageResolver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        String customMessage = "MessageResolver를 사용하지 않은 커스텀 메세지";

        // when & then
        mockMvc.perform(get("/test/app-ex-custom"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(customMessage));
    }

    @Test
    @DisplayName("[POST] /test/invalid-request-body - 요청 데이터 검증 실패 시 400 VALIDATION_ERROR 반환")
    void postShouldReturnValidationErrorWhenRequestBodyIsInvalid() throws Exception {
        // given
        String code = "VALIDATION_ERROR";
        String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
        given(messageResolver.resolve(code)).willReturn(message);
        String request = """
                {
                    "name" : "",
                    "age" : -1
                }
                """;

        // when & then
        mockMvc.perform(post("/test/invalid-request-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("[GET] /test/invalid-path-variable/{id} - 요청 데이터 검증 실패 시 400 VALIDATION_ERROR 반환")
    void getShouldReturnValidationErrorWhenPathVariableIsBlank() throws Exception {
        // given
        String code = "VALIDATION_ERROR";
        String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
        given(messageResolver.resolve(code)).willReturn(message);
        // when & then
        mockMvc.perform(get("/test/invalid-path-variable/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("[GET] /test/invalid-request-parm?id= - 요청 데이터 검증 실패 시 400 VALIDATION_ERROR 반환")
    void getShouldReturnValidationErrorWhenRequestParamIsBlank() throws Exception {
        // given
        String code = "VALIDATION_ERROR";
        String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
        given(messageResolver.resolve(code)).willReturn(message);
        // when & then
        mockMvc.perform(get("/test/invalid-request-parm")
                        .param("id", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.details").isArray())
                .andDo(print());
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
        @NotBlank
        String name;
        @Min(1)
        int age;
    }

}