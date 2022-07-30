package com.prgrms.offer.authentication.presentation;

import com.prgrms.offer.authentication.application.AuthService;
import com.prgrms.offer.authentication.application.response.OAuthLoginUrlResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.request.TokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/authorization/kakao")
    public ResponseEntity<OAuthLoginUrlResponse> showLoginUrl() {
        return ResponseEntity.ok(authService.getLoginUrl());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody TokenRequest request) {
        TokenResponse response = authService.createToken(request);
        return ResponseEntity.ok(response);
    }
}
