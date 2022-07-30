package com.prgrms.offer.authentication.application;

import com.prgrms.offer.authentication.application.response.OAuthLoginUrlResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.request.TokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final OAuth2Client oAuth2Client;

    public AuthService(final OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }

    public OAuthLoginUrlResponse getLoginUrl() {
        return new OAuthLoginUrlResponse(oAuth2Client.getLoginUrl());
    }

    public TokenResponse createToken(TokenRequest request) {
        // TODO: 2022/07/28 회원정보 조회 및 저장, 토큰 생성 후 반환
        return null;
    }
}
