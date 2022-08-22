package com.prgrms.offer;

import com.prgrms.offer.authentication.application.OAuthWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestConfig {

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

    @Bean
    @Profile("test")
    public OAuthWebClient oAuthWebClient() {
        return new FakeWebClientForKakao(clientId, clientSecret, redirectUrl, oauthServerUrl, accessTokenRequestUrl,
                userProfileRequestUrl);
    }
}
