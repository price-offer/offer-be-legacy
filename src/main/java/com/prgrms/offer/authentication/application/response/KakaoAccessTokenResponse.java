package com.prgrms.offer.authentication.application.response;

import lombok.Getter;

@Getter
public class KakaoAccessTokenResponse {

    private String accessToken;

    public KakaoAccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static KakaoAccessTokenResponse from(String accessToken) {
        return new KakaoAccessTokenResponse(accessToken);
    }
}
