package com.prgrms.offer.documentation.authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.offer.authentication.application.response.OAuthLoginUrlResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.AuthController;
import com.prgrms.offer.documentation.ApiDocumentationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
public class AuthDocumentationTest extends ApiDocumentationTest {

    @Test
    @DisplayName("카카오 로그인 URL 조회")
    void getKakaoLoginUrl() throws Exception {
        given(authService.getLoginUrl())
                .willReturn(new OAuthLoginUrlResponse(
                        "https://kauth.kakao.com/oauth/authorize?client_id=awergkjaarg&redirect_uri=redirect-url.com&response_type=code"));

        ResultActions result = mockMvc.perform(
                get("/api/authorization/kakao")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/kakao/login-url",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("url").type(JsonFieldType.STRING).description("카카오 인증서버 URL")
                        )));
    }

    @Test
    @DisplayName("Oauth 인증 후 토큰 생성")
    void createToken() throws Exception {
        given(authService.createToken(any()))
                .willReturn(TokenResponse.of("jwt.token.here", false));

        Request request = new Request();
        request.code = "auth_code_from_kakao_oauth_server";

        ResultActions result = mockMvc.perform(
                post("/api/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/create-token",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("OAuth 인증 이후 전달받은 인증코드")
                        ),
                        responseFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("JWT 토큰"),
                                fieldWithPath("alreadyJoined").type(JsonFieldType.BOOLEAN).description("회원가입 이력 여부")
                        )
                ));
    }

    public static class Request {
        String code;

        public Request() {
        }

        public String getCode() {
            return code;
        }
    }
}
