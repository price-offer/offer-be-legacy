package com.prgrms.offer.authentication.application.response;

import lombok.Getter;

@Getter
public class KakaoAccessTokenResponse {

    private String accessToken;

    public KakaoAccessTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}
