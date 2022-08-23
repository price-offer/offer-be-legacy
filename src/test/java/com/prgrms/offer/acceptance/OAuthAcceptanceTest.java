package com.prgrms.offer.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.prgrms.offer.authentication.application.OAuthWebClient;
import com.prgrms.offer.authentication.application.response.KakaoAccessTokenResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.request.TokenRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class OAuthAcceptanceTest extends AcceptanceTest {

    @Value("${oauth2.kakao.client.id}")
    private String clientId;

    @Value("${oauth2.kakao.client.secret}")
    private String clientSecret;

    @Value("${oauth2.kakao.url.redirect}")
    private String redirectUrl;

    @Value("${oauth2.kakao.url.oauth-server}")
    private String oauthServerUrl;

    @Value("${oauth2.kakao.url.accessToken}")
    private String accessTokenRequestUrl;

    @Value("${oauth2.kakao.url.userProfile}")
    private String userProfileRequestUrl;

    @MockBean
    private OAuthWebClient oAuthWebClient;

    @Test
    @DisplayName("kakaoLogin Url을 요청하면 로그인에 사용할 Url을 응답한다")
    void getKakaoLoginUrl() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/authorization/kakao")
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getString("url")).startsWith(oauthServerUrl)
        );
    }

    @Test
    @DisplayName("첫 로그인 사용자는 로그인 시 alreadyJoin 필드가 false이다")
    void responseSocialProfile() {
        TokenRequest request = new TokenRequest("authCode");

        when(oAuthWebClient.exchange(eq(accessTokenRequestUrl), eq(HttpMethod.POST), any(),
                eq(KakaoAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(KakaoAccessTokenResponse.from("accessToken")));
        when(oAuthWebClient.exchange(eq(userProfileRequestUrl), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\n"
                        + "    \"id\":1234,\n"
                        + "    \"kakao_account\": {\n"
                        + "        \"profile_nickname_needs_agreement    \": false,\n"
                        + "        \"profile_image_needs_agreement    \": false,\n"
                        + "        \"profile\": {\n"
                        + "            \"nickname\": \"홍길동\",\n"
                        + "            \"thumbnail_image_url\": \"http://yyy.kakao.com/.../img_110x110.jpg\",\n"
                        + "            \"profile_image_url\": \"http://test.jpg\",\n"
                        + "            \"is_default_image\":false\n"
                        + "        },\n"
                        + "        \"name_needs_agreement\":false, \n"
                        + "        \"name\":\"홍길동\"\n"
                        + "    }\n"
                        + "}"));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/login")
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(TokenResponse.class).getToken()).isNotNull(),
                () -> assertThat(response.as(TokenResponse.class).isAlreadyJoined()).isFalse()
        );
    }

    @Test
    @DisplayName("이미 가입된 사용자는 alreadyJoined 필드가 true이다")
    void responseAlreadyJoinedTrueForExistsMember() {
        TokenRequest request = new TokenRequest("authCode");

        when(oAuthWebClient.exchange(eq(accessTokenRequestUrl), eq(HttpMethod.POST), any(),
                eq(KakaoAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(KakaoAccessTokenResponse.from("accessToken")));
        when(oAuthWebClient.exchange(eq(userProfileRequestUrl), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\n"
                        + "    \"id\":1234,\n"
                        + "    \"kakao_account\": {\n"
                        + "        \"profile_nickname_needs_agreement    \": false,\n"
                        + "        \"profile_image_needs_agreement    \": false,\n"
                        + "        \"profile\": {\n"
                        + "            \"nickname\": \"홍길동\",\n"
                        + "            \"thumbnail_image_url\": \"http://yyy.kakao.com/.../img_110x110.jpg\",\n"
                        + "            \"profile_image_url\": \"http://test.jpg\",\n"
                        + "            \"is_default_image\":false\n"
                        + "        },\n"
                        + "        \"name_needs_agreement\":false, \n"
                        + "        \"name\":\"홍길동\"\n"
                        + "    }\n"
                        + "}"));

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/login")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/login")
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(TokenResponse.class).getToken()).isNotNull(),
                () -> assertThat(response.as(TokenResponse.class).isAlreadyJoined()).isTrue()
        );
    }
}
