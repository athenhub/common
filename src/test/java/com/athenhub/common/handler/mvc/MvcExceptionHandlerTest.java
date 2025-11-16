package com.athenhub.common.handler.mvc;

import com.athenhub.common.error.GlobalErrorCode;
import com.athenhub.common.message.MessageResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {MvcExceptionHandler.class,
        TestController.class
})
@WebMvcTest
class MvcExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageResolver messageResolver;

    @Test
    @DisplayName("[GET] /test/app-ex - ApplicationException 발생 시 404 NOT_FOUND 반환")
    void handleApplicationException() throws Exception {
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
    void handleApplicationException_WithCustomMessage() throws Exception {
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
    void handleMethodArgumentNotValidException() throws Exception {
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
    void handleHandlerMethodValidationException_WithInvalidPathVariable() throws Exception {
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
    void handleHandlerMethodValidationException_WithInvalidRequestParam() throws Exception {
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
    void handleAllUncaughtException() throws Exception {
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

    @Test
    @DisplayName("405 MethodNotAllowed 처리: GET → POST 전용 엔드포인트 호출")
    void handleMethodNotAllowed() throws Exception {
        // given
        given(messageResolver.resolve(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), "POST"))
                .willReturn("지원되지 않는 메서드입니다. 허용: POST");

        // when & then
        mockMvc.perform(get("/test/invalid-method"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.message").value("지원되지 않는 메서드입니다. 허용: POST"));
    }

    @Test
    @DisplayName("400 JSON Parse Error : 잘못된 JSON 전달")
    void handleJsonParse() throws Exception {
        // given
        String code = "INVALID_JSON";
        String message = "JSON 파싱 오류";
        given(messageResolver.resolve(code))
                .willReturn(message);

        // when & then
        mockMvc.perform(post("/test/invalid-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")) // 파싱 불가능 JSON
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    @DisplayName("400 TypeMismatch : PathVariable 타입 불일치(Long ← 문자열)")
    void handleTypeMismatch() throws Exception {
        // given
        String code = "TYPE_MISMATCH";
        String message = "파라미터 id의 값 abc는 올바르지 않습니다.";
        given(messageResolver.resolve(GlobalErrorCode.TYPE_MISMATCH.getCode(), "id", "abc"))
                .willReturn(message);

        // when & then
        mockMvc.perform(get("/test/mismatch/abc")) // Long → abc 변환 실패
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }


}