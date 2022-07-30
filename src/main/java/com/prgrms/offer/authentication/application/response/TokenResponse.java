package com.prgrms.offer.authentication.application.response;

import lombok.Getter;

@Getter
public class TokenResponse {

    private String token;

    private TokenResponse() {
    }

    private TokenResponse(String token) {
        this.token = token;
    }

    public static TokenResponse from(String token) {
        return new TokenResponse(token);
    }
}
