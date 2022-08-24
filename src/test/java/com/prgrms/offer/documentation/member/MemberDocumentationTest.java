package com.prgrms.offer.documentation.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.offer.documentation.ApiDocumentationTest;
import com.prgrms.offer.domain.member.controller.MemberController;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.service.response.ActivityResponse;
import com.prgrms.offer.domain.member.service.response.MemberProfileResponse;
import com.prgrms.offer.domain.member.service.response.MyActivityResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
public class MemberDocumentationTest extends ApiDocumentationTest {

    @Test
    @DisplayName("내 프로필 조회")
    void getProfileOfMine() throws Exception {

        given(authenticationContext.getPrincipal())
                .willReturn("1");

        given(memberService.getProfile(1L))
                .willReturn(MemberProfileResponse.from(Member.builder()
                        .id(1L)
                        .nickname("행복한 냉장고 3호")
                        .profileImageUrl("http://image.jpg")
                        .offerLevel(1)
                        .build()));

        ResultActions result = mockMvc.perform(
                get("/api/members/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("members/get-my-profile",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("offerLevel").type(JsonFieldType.NUMBER).description("오퍼 레벨"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("사용자 프로필 이미지")
                        )
                ));
    }

    @Test
    @DisplayName("사용자 프로필 조회")
    void getMemberProfile() throws Exception {

        given(memberService.getProfile(1L))
                .willReturn(MemberProfileResponse.from(Member.builder()
                        .id(1L)
                        .nickname("행복한 냉장고 3호")
                        .profileImageUrl("http://image.jpg")
                        .offerLevel(1)
                        .build()));

        ResultActions result = mockMvc.perform(
                get("/api/members/{memberId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("members/get-profile",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사용자 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("offerLevel").type(JsonFieldType.NUMBER).description("오퍼 레벨"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("사용자 프로필 이미지")
                        )
                ));
    }

    @Test
    @DisplayName("내 활동 조회")
    void getActivityOfMine() throws Exception {
        given(authenticationContext.getPrincipal())
                .willReturn("1");

        given(memberService.getMyActivity(1L))
                .willReturn(MyActivityResponse.of(3L, 5L, 1L, 1L));

        ResultActions result = mockMvc.perform(
                get("/api/members/activity")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("members/get-my-activity",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        responseFields(
                                fieldWithPath("sellingArticleCount").type(JsonFieldType.NUMBER)
                                        .description("판매 중인 상품 수"),
                                fieldWithPath("likedArticleCount").type(JsonFieldType.NUMBER).description("관심 상품 수"),
                                fieldWithPath("soldArticleCount").type(JsonFieldType.NUMBER).description("판매 완료 상품 수"),
                                fieldWithPath("reviewCount").type(JsonFieldType.NUMBER).description("거래 후기 수")
                        )
                ));
    }

    @Test
    @DisplayName("사용자 활동 조회")
    void getActivity() throws Exception {
        given(memberService.getActivity(1L))
                .willReturn(ActivityResponse.of(3L, 5L, 1L));

        ResultActions result = mockMvc.perform(
                get("/api/members/{memberId}/activity", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("members/get-activity",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("사용자 아이디")
                        ),
                        responseFields(
                                fieldWithPath("sellingArticleCount").type(JsonFieldType.NUMBER)
                                        .description("판매 중인 상품 수"),
                                fieldWithPath("soldArticleCount").type(JsonFieldType.NUMBER).description("판매 완료 상품 수"),
                                fieldWithPath("reviewCount").type(JsonFieldType.NUMBER).description("거래 후기 수")
                        )
                ));
    }

    @Test
    @DisplayName("회원 정보 수정")
    void changeProfile() throws Exception {
        given(authenticationContext.getPrincipal())
                .willReturn("1");

        Request request = new Request();
        request.nickname = "새로운 닉네임";
        request.profileImageUrl = "http://newImage.jpg";

        ResultActions result = mockMvc.perform(
                put("/api/members/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("members/change-profile",
                        getDocumentRequest(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                        .description("변경할 프로필 이미지 URL")
                        )
                ));
    }

    public static class Request {
        String nickname;
        String profileImageUrl;

        public Request() {
        }

        public String getNickname() {
            return nickname;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }
    }

}
