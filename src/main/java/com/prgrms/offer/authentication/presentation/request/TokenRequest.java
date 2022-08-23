package com.prgrms.offer.authentication.presentation.request;

import lombok.Getter;

@Getter
public class TokenRequest {

    private String code;

    private TokenRequest() {
    }

    public TokenRequest(final String code) {
        this.code = code;
    }
}
