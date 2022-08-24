package com.prgrms.offer.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prgrms.offer.TestConfig;
import com.prgrms.offer.authentication.application.response.SocialProfileResponse;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class KakaoOAuthClientTest {

    @Value("${oauth2.kakao.url.oauth-server}")
    private String oauthServerUrl;

    @Autowired
    private KakaoOAuthClient kakaoOAuthClient;

    @Test
    @DisplayName("카카오 인증 로그인 URL을 반환한다")
    void getLoginUrl() {
        String loginUrl = kakaoOAuthClient.getLoginUrl();
        assertThat(loginUrl).startsWith(oauthServerUrl);
    }

    // 검증 값은 FakeWebClientForKakao 클래스 참조
    @Test
    @DisplayName("인증 코드를 입력받아 사용자 프로필 정보를 반환한다")
    void name() {
        SocialProfileResponse socialProfile = kakaoOAuthClient.requestSocialProfile("아무래도좋을인증코드");

        assertAll(
                () -> assertThat(socialProfile.getOauthId()).isEqualTo(1234L),
                () -> assertThat(socialProfile.getOauthType()).isEqualTo(OAuthType.KAKAO),
                () -> assertThat(socialProfile.getProfileImageUrl()).isEqualTo("http://test.jpg")
        );
    }
}
