package com.prgrms.offer.authentication.application.response;

import lombok.Getter;

@Getter
public class OAuthLoginUrlResponse {

    private String url;

    public OAuthLoginUrlResponse() {
    }

    public OAuthLoginUrlResponse(final String url) {
        this.url = url;
    }
}
