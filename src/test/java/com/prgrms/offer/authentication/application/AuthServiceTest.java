package com.prgrms.offer.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.prgrms.offer.authentication.application.response.OAuthLoginUrlResponse;
import com.prgrms.offer.authentication.application.response.SocialProfileResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.request.TokenRequest;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AuthServiceTest {

    @Value("${oauth2.kakao.url.oauth-server}")
    private String oauthServerUrl;

    @MockBean
    private KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("인증 서버 url을 담은 응답을 반환한다")
    void getLoginUrl() {
        when(kakaoOAuthClient.getLoginUrl()).thenReturn(oauthServerUrl);

        OAuthLoginUrlResponse response = authService.getLoginUrl();

        assertThat(response.getUrl()).startsWith(oauthServerUrl);
    }

    @Test
    @DisplayName("처음 가입하는 회원일 경우, 회원을 생성하고 alreadyJoined 필드가 false인 토큰 응답을 반환한다")
    void returnTokenResponseWithCreatingUserAlreadyJoinedFalseForFirstUser() {
        when(kakaoOAuthClient.requestSocialProfile(anyString())).thenReturn(
                SocialProfileResponse.of("kakao", 1L, "http://test.jpg"));

        TokenResponse response = authService.createToken(new TokenRequest("authCode"));

        assertAll(
                () -> assertThat(response.getToken()).isNotNull(),
                () -> assertThat(response.isAlreadyJoined()).isFalse(),
                () -> assertThat(memberRepository.existsByOauthTypeAndOauthId("kakao", 1L)).isTrue()
        );
    }
}
