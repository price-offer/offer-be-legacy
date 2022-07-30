package com.prgrms.offer.authentication.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoOAuthClient implements OAuth2Client {

    private static final String OAUTH_LOGIN_URL_SUFFIX =
            "?client_id=%s&redirect_uri=%s&response_type=code";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String oauthServerUrl;

    public KakaoOAuthClient(@Value("oauth.kakao.client.id") final String clientId,
                            @Value("oauth.kakao.client.secret") final String clientSecret,
                            @Value("oauth.kakao.url.redirect") final String redirectUrl,
                            @Value("oauth.kakao.url.oauth-server") final String oauthServerUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.oauthServerUrl = oauthServerUrl;
    }

    @Override
    public String getLoginUrl() {
        String oauthLoginUrl = oauthServerUrl + OAUTH_LOGIN_URL_SUFFIX;
        return String.format(oauthLoginUrl, clientId, redirectUrl);
    }
}
