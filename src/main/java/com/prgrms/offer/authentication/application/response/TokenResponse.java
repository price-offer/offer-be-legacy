package com.prgrms.offer.authentication.application.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {

    private final String token;
    private final boolean alreadyJoined;

    public static TokenResponse of (String token, boolean alreadyJoined) {
        return new TokenResponse(token, alreadyJoined);
    }
}
