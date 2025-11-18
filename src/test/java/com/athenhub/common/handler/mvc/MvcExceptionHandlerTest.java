package com.athenhub.common.handler.mvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

/** MvcExceptionHandler Test. */
@ContextConfiguration(classes = {MvcExceptionHandler.class, TestController.class})
@WebMvcTest
public class MvcExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private MessageResolver messageResolver;

  @Test
  @DisplayName("ApplicationException 발생 — ErrorCode에 정의한 status, messageResolve에 의해 변환된 메세지 반환")
  void handleApplicationException() throws Exception {
    // given
    String code = "NOT_FOUND";
    String message = "요청하신 리소스를 찾을 수 없습니다.";
    given(messageResolver.resolve(code)).willReturn(message);

    // when & then
    mockMvc
        .perform(get("/test/app-ex"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message));
  }

  @Test
  @DisplayName("ApplicationException(customMessage) 발생 - ErrorCode에 정의한 status, customMessage 반환")
  void handleApplicationExceptionWithCustomMessage() throws Exception {
    // given
    String code = "NOT_FOUND";
    String customMessage = "MessageResolver를 사용하지 않은 커스텀 메세지";

    // when & then
    mockMvc
        .perform(get("/test/app-ex-custom"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(customMessage));
  }

  @Test
  @DisplayName("JSON Body 검증 실패 — 400 VALIDATION_ERROR 반환")
  void handleMethodArgumentNotValidException() throws Exception {
    // given
    String code = "VALIDATION_ERROR";
    String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
    given(messageResolver.resolve(code)).willReturn(message);
    String request =
        """
                {
                    "name" : "",
                    "age" : -1
                }
                """;

    // when & then
    mockMvc
        .perform(
            post("/test/invalid-request-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message))
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  @DisplayName("PathVariable 검증 실패 — 400 VALIDATION_ERROR 반환")
  void handleHandlerMethodValidationExceptionWithInvalidPathVariable() throws Exception {
    // given
    String code = "VALIDATION_ERROR";
    String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
    given(messageResolver.resolve(code)).willReturn(message);
    // when & then
    mockMvc
        .perform(get("/test/invalid-path-variable/ "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message))
        .andExpect(jsonPath("$.details").isArray());
  }

  @Test
  @DisplayName("RequestParam 검증 실패 — 400 VALIDATION_ERROR 반환")
  void handleHandlerMethodValidationExceptionWithInvalidRequestParam() throws Exception {
    // given
    String code = "VALIDATION_ERROR";
    String message = "요청 데이터가 유효하지 않습니다. 잘못된 항목을 확인해주세요.";
    given(messageResolver.resolve(code)).willReturn(message);
    // when & then
    mockMvc
        .perform(get("/test/invalid-request-parm").param("id", ""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message))
        .andExpect(jsonPath("$.details").isArray())
        .andDo(print());
  }

  @Test
  @DisplayName("알 수 없는 예외 발생 — 500 INTERNAL_SERVER_ERROR 반환")
  void handleAllUncaughtException() throws Exception {
    // given
    String code = "INTERNAL_SERVER_ERROR";
    String message = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
    given(messageResolver.resolve(code)).willReturn(message);

    // when & then
    mockMvc
        .perform(get("/test/ex"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message));
  }

  @Test
  @DisplayName("지원되지 않는 HTTP Method — 405 METHOD_NOT_ALLOWED 반환")
  void handleMethodNotAllowed() throws Exception {
    // given
    given(messageResolver.resolve(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), "POST"))
        .willReturn("지원되지 않는 메서드입니다. 허용: POST");

    // when & then
    mockMvc
        .perform(get("/test/invalid-method"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.message").value("지원되지 않는 메서드입니다. 허용: POST"));
  }

  @Test
  @DisplayName("JSON Parse 오류 — 400 INVALID_JSON 반환")
  void handleJsonParse() throws Exception {
    // given
    String code = "INVALID_JSON";
    String message = "JSON 파싱 오류";
    given(messageResolver.resolve(code)).willReturn(message);

    // when & then
    mockMvc
        .perform(
            post("/test/invalid-json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")) // 파싱 불가능 JSON
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message));
  }

  @Test
  @DisplayName("타입 불일치(TypeMismatch) — 400 TYPE_MISMATCH 반환")
  void handleTypeMismatch() throws Exception {
    // given
    String code = "TYPE_MISMATCH";
    String message = "파라미터 id의 값 abc는 올바르지 않습니다.";
    given(messageResolver.resolve(GlobalErrorCode.TYPE_MISMATCH.getCode(), "id", "abc"))
        .willReturn(message);

    // when & then
    mockMvc
        .perform(get("/test/mismatch/abc")) // Long → abc 변환 실패
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(code))
        .andExpect(jsonPath("$.message").value(message));
  }
}
